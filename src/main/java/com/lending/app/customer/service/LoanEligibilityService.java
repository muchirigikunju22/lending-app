package com.lending.app.customer.service;

import com.lending.app.common.exception.BusinessRuleException;
import com.lending.app.customer.domain.model.Customer;
import com.lending.app.customer.domain.model.LoanLimit;
import com.lending.app.loan.domain.model.Loan;
import com.lending.app.loan.domain.model.LoanState;
import com.lending.app.loan.domain.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanEligibilityService {

    private final LoanRepository loanRepository;

    public void validateLoanRequest(Customer customer, BigDecimal requestedAmount) {
        if (customer.getLoanLimit() == null) {
            throw new BusinessRuleException("Customer does not have loan limits configured");
        }

        LoanLimit limit = customer.getLoanLimit();
        validateLoanLimit(requestedAmount, limit);
        validateOutstandingBalance(customer.getId(), requestedAmount, limit);
        validateActiveLoanCount(customer.getId(), limit);
    }

    private void validateLoanLimit(BigDecimal requestedAmount, LoanLimit limit) {
        if (requestedAmount.compareTo(limit.getMaxSingleLoan()) > 0) {
            throw new BusinessRuleException(
                    String.format("Requested amount %s exceeds maximum single loan limit of %s",
                            requestedAmount, limit.getMaxSingleLoan()));
        }
    }

    private void validateOutstandingBalance(Long customerId, BigDecimal requestedAmount, LoanLimit limit) {
        BigDecimal totalOutstanding = loanRepository.findByCustomerIdAndStateIn(
                        customerId, List.of(LoanState.OPEN, LoanState.OVERDUE))
                .stream()
                .map(Loan::getOutstandingBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal projectedTotal = totalOutstanding.add(requestedAmount);
        if (projectedTotal.compareTo(limit.getMaxTotalOutstanding()) > 0) {
            throw new BusinessRuleException(
                    String.format("Projected outstanding balance %s would exceed maximum of %s",
                            projectedTotal, limit.getMaxTotalOutstanding()));
        }
    }

    private void validateActiveLoanCount(Long customerId, LoanLimit limit) {
        long activeLoanCount = loanRepository.countByCustomerIdAndStateIn(
                customerId, List.of(LoanState.OPEN, LoanState.OVERDUE));

        if (activeLoanCount >= limit.getMaxActiveLoans()) {
            throw new BusinessRuleException(
                    String.format("Customer has reached maximum active loan limit of %d",
                            limit.getMaxActiveLoans()));
        }
    }
}
