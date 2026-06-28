package com.lending.app.loan.service;

import com.lending.app.loan.domain.model.*;
import com.lending.app.loan.domain.repository.*;
import com.lending.app.loan.service.statemachine.LoanStateMachine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepaymentServiceTest {

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private RepaymentRepository repaymentRepository;
    @Mock
    private InstallmentRepository installmentRepository;
    @Mock
    private LoanEventHistoryRepository eventHistoryRepository;
    @Mock
    private LoanStateMachine stateMachine;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private RepaymentService repaymentService;

    @Test
    void shouldMakePayment() {
        Long loanId = 1L;
        Loan loan = createTestLoan(loanId, new BigDecimal("1000"));

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(loan);

        Repayment result = repaymentService.makePayment(loanId, new BigDecimal("500"), "BANK_TRANSFER");

        assertNotNull(result);
        assertEquals(new BigDecimal("500"), result.getAmount());
        assertEquals(new BigDecimal("500"), loan.getOutstandingBalance());
    }

    @Test
    void shouldFullyRepayAndCloseLoan() {
        Long loanId = 1L;
        Loan loan = createTestLoan(loanId, new BigDecimal("1000"));

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(loan);

        Repayment result = repaymentService.makePayment(loanId, new BigDecimal("1000"), "BANK_TRANSFER");

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, loan.getOutstandingBalance());
    }

    @Test
    void shouldRejectPaymentExceedingBalance() {
        Long loanId = 1L;
        Loan loan = createTestLoan(loanId, new BigDecimal("100"));

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        assertThrows(com.lending.app.common.exception.BusinessRuleException.class,
                () -> repaymentService.makePayment(loanId, new BigDecimal("200"), "BANK"));
    }

    @Test
    void shouldRejectPaymentOnClosedLoan() {
        Long loanId = 1L;
        Loan loan = createTestLoan(loanId, BigDecimal.ZERO);
        loan.setState(LoanState.CLOSED);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        assertThrows(com.lending.app.common.exception.BusinessRuleException.class,
                () -> repaymentService.makePayment(loanId, new BigDecimal("100"), "BANK"));
    }

    private Loan createTestLoan(Long id, BigDecimal outstandingBalance) {
        Loan loan = Loan.builder()
                .id(id)
                .loanNumber("TEST-" + id)
                .outstandingBalance(outstandingBalance)
                .principalAmount(outstandingBalance)
                .state(LoanState.OPEN)
                .totalRepaid(BigDecimal.ZERO)
                .totalFeesAccrued(BigDecimal.ZERO)
                .customer(com.lending.app.customer.domain.model.Customer.builder().id(1L).build())
                .loanProduct(com.lending.app.product.domain.model.LoanProduct.builder().id(1L).build())
                .billingCycle(BillingCycleType.INDIVIDUAL)
                .installments(new java.util.ArrayList<>())
                .repayments(new java.util.ArrayList<>())
                .build();
        return loan;
    }
}
