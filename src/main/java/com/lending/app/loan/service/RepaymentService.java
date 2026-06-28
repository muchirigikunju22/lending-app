package com.lending.app.loan.service;

import com.lending.app.common.event.LoanClosedEvent;
import com.lending.app.common.event.PaymentReceivedEvent;
import com.lending.app.common.exception.BusinessRuleException;
import com.lending.app.common.exception.ResourceNotFoundException;
import com.lending.app.loan.domain.model.*;
import com.lending.app.loan.domain.repository.InstallmentRepository;
import com.lending.app.loan.domain.repository.LoanEventHistoryRepository;
import com.lending.app.loan.domain.repository.LoanRepository;
import com.lending.app.loan.domain.repository.RepaymentRepository;
import com.lending.app.loan.service.statemachine.LoanStateMachine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RepaymentService {

    private final LoanRepository loanRepository;
    private final RepaymentRepository repaymentRepository;
    private final InstallmentRepository installmentRepository;
    private final LoanEventHistoryRepository eventHistoryRepository;
    private final LoanStateMachine stateMachine;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Repayment makePayment(Long loanId, BigDecimal amount, String paymentMethod) {
        log.info("Processing payment of {} for loan: {}", amount, loanId);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Payment amount must be greater than zero");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", loanId));

        if (loan.isClosed() || loan.isCancelled() || loan.getState() == LoanState.WRITTEN_OFF) {
            throw new BusinessRuleException("Cannot make payment on loan in state: " + loan.getState());
        }

        if (amount.compareTo(loan.getOutstandingBalance()) > 0) {
            throw new BusinessRuleException(
                    String.format("Payment amount %s exceeds outstanding balance %s", amount, loan.getOutstandingBalance()));
        }

        Repayment repayment = Repayment.builder()
                .loan(loan)
                .amount(amount)
                .paymentMethod(paymentMethod != null ? paymentMethod : "BANK_TRANSFER")
                .transactionReference(UUID.randomUUID().toString())
                .paymentDate(Instant.now())
                .build();

        distributePayment(loan, amount, repayment);

        loan.addRepayment(repayment);
        Loan savedLoan = loanRepository.save(loan);

        if (loan.isOverdue() && loan.getOutstandingBalance().compareTo(BigDecimal.ZERO) > 0) {
            stateMachine.transition(loan, LoanState.OPEN);
        }

        if (loan.isFullyPaid()) {
            stateMachine.transition(loan, LoanState.CLOSED);
            recordEvent(loan, LoanEventHistory.LoanEventType.LOAN_CLOSED,
                    "Loan fully repaid and closed", loan.getTotalRepaid(), LoanState.OPEN.name(), LoanState.CLOSED.name());

            eventPublisher.publishEvent(new LoanClosedEvent(this, loan.getId(),
                    loan.getCustomer().getId(), loan.getTotalRepaid(), "Full repayment"));
        }

        recordEvent(loan, LoanEventHistory.LoanEventType.PAYMENT_RECEIVED,
                "Payment received: " + amount, amount, null, savedLoan.getState().name());

        eventPublisher.publishEvent(new PaymentReceivedEvent(this, loan.getId(),
                loan.getCustomer().getId(), repayment.getId(), amount, loan.getOutstandingBalance()));

        return repayment;
    }

    @Transactional
    public Repayment makePaymentTowardInstallment(Long loanId, Long installmentId, BigDecimal amount, String paymentMethod) {
        log.info("Processing payment of {} toward installment {} for loan: {}", amount, installmentId, loanId);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Payment amount must be greater than zero");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", loanId));

        Installment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Installment", installmentId));

        if (!installment.getLoan().getId().equals(loanId)) {
            throw new BusinessRuleException("Installment does not belong to this loan");
        }

        if (installment.isFullyPaid()) {
            throw new BusinessRuleException("Installment is already fully paid");
        }

        if (loan.isClosed() || loan.isCancelled() || loan.getState() == LoanState.WRITTEN_OFF) {
            throw new BusinessRuleException("Cannot make payment on loan in state: " + loan.getState());
        }

        if (amount.compareTo(loan.getOutstandingBalance()) > 0) {
            throw new BusinessRuleException(
                    String.format("Payment amount %s exceeds outstanding balance %s", amount, loan.getOutstandingBalance()));
        }

        BigDecimal appliedAmount = amount.min(installment.getRemainingAmount());

        Repayment repayment = Repayment.builder()
                .loan(loan)
                .installment(installment)
                .amount(appliedAmount)
                .paymentMethod(paymentMethod != null ? paymentMethod : "BANK_TRANSFER")
                .transactionReference(UUID.randomUUID().toString())
                .paymentDate(Instant.now())
                .build();

        installment.addPayment(appliedAmount);
        installmentRepository.save(installment);

        if (appliedAmount.compareTo(amount) < 0) {
            BigDecimal remainder = amount.subtract(appliedAmount);
            distributeRemaining(loan, remainder, repayment);
        }

        loan.addRepayment(repayment);
        Loan savedLoan = loanRepository.save(loan);

        if (loan.isOverdue() && loan.getOutstandingBalance().compareTo(BigDecimal.ZERO) > 0) {
            boolean hasOverdueInstallments = loan.getInstallments().stream()
                    .anyMatch(i -> i.getStatus() == InstallmentStatus.OVERDUE);
            if (!hasOverdueInstallments) {
                stateMachine.transition(loan, LoanState.OPEN);
            }
        }

        if (loan.isFullyPaid()) {
            stateMachine.transition(loan, LoanState.CLOSED);
            recordEvent(loan, LoanEventHistory.LoanEventType.LOAN_CLOSED,
                    "Loan fully repaid and closed", loan.getTotalRepaid(), LoanState.OPEN.name(), LoanState.CLOSED.name());

            eventPublisher.publishEvent(new LoanClosedEvent(this, loan.getId(),
                    loan.getCustomer().getId(), loan.getTotalRepaid(), "Full repayment"));
        }

        recordEvent(loan, LoanEventHistory.LoanEventType.PAYMENT_RECEIVED,
                "Payment received: " + amount, amount, null, savedLoan.getState().name());

        eventPublisher.publishEvent(new PaymentReceivedEvent(this, loan.getId(),
                loan.getCustomer().getId(), repayment.getId(), amount, loan.getOutstandingBalance()));

        return repayment;
    }

    public List<Repayment> getRepayments(Long loanId) {
        return repaymentRepository.findByLoanId(loanId);
    }

    private void distributePayment(Loan loan, BigDecimal amount, Repayment repayment) {
        BigDecimal remaining = amount;

        for (Installment installment : loan.getInstallments()) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            if (installment.isFullyPaid()) continue;

            BigDecimal toApply = remaining.min(installment.getRemainingAmount());
            installment.addPayment(toApply);
            remaining = remaining.subtract(toApply);

            if (repayment.getInstallment() == null) {
                repayment.setInstallment(installment);
            }
        }
    }

    private void distributeRemaining(Loan loan, BigDecimal remainder, Repayment mainRepayment) {
        for (Installment installment : loan.getInstallments()) {
            if (remainder.compareTo(BigDecimal.ZERO) <= 0) break;
            if (installment.isFullyPaid()) continue;

            BigDecimal toApply = remainder.min(installment.getRemainingAmount());
            installment.addPayment(toApply);
            remainder = remainder.subtract(toApply);
        }
    }

    private void recordEvent(Loan loan, LoanEventHistory.LoanEventType eventType, String description,
                             BigDecimal amount, String previousState, String newState) {
        LoanEventHistory event = LoanEventHistory.builder()
                .loanId(loan.getId())
                .eventType(eventType)
                .description(description)
                .amount(amount)
                .previousState(previousState)
                .newState(newState)
                .occurredAt(Instant.now())
                .build();
        eventHistoryRepository.save(event);
    }
}
