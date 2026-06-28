package com.lending.app.loan.service;

import com.lending.app.common.event.*;
import com.lending.app.common.exception.BusinessRuleException;
import com.lending.app.common.exception.DuplicateOperationException;
import com.lending.app.common.exception.ResourceNotFoundException;
import com.lending.app.common.model.IdempotencyKey;
import com.lending.app.customer.domain.model.BillingProfile;
import com.lending.app.customer.domain.model.Customer;
import com.lending.app.customer.domain.repository.BillingProfileRepository;
import com.lending.app.customer.domain.repository.CustomerRepository;
import com.lending.app.customer.service.LoanEligibilityService;
import com.lending.app.loan.domain.model.*;
import com.lending.app.loan.domain.repository.*;
import com.lending.app.loan.service.schedule.LoanScheduleGenerator;
import com.lending.app.loan.service.schedule.ScheduleGeneratorFactory;
import com.lending.app.loan.service.statemachine.LoanStateMachine;
import com.lending.app.product.domain.model.LoanProduct;
import com.lending.app.product.domain.repository.ProductRepository;
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
public class LoanService {

    private final LoanRepository loanRepository;
    private final InstallmentRepository installmentRepository;
    private final RepaymentRepository repaymentRepository;
    private final LoanEventHistoryRepository eventHistoryRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final BillingProfileRepository billingProfileRepository;
    private final LoanStateMachine stateMachine;
    private final ScheduleGeneratorFactory scheduleGeneratorFactory;
    private final LoanEligibilityService loanEligibilityService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Loan createLoan(Long customerId, Long productId, BigDecimal principalAmount,
                           BillingCycleType billingCycle, String idempotencyKeyValue) {
        log.info("Creating loan for customer: {} with product: {}", customerId, productId);

        if (idempotencyKeyValue != null) {
            loanRepository.findByIdempotencyKey_Value(idempotencyKeyValue).ifPresent(existing -> {
                throw new DuplicateOperationException(
                        "Loan already exists with idempotency key: " + idempotencyKeyValue);
            });
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        LoanProduct product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("LoanProduct", productId));

        if (!product.isActive()) {
            throw new BusinessRuleException("Product is not active: " + productId);
        }

        if (principalAmount.compareTo(product.getMinAmount()) < 0 ||
                principalAmount.compareTo(product.getMaxAmount()) > 0) {
            throw new BusinessRuleException(String.format(
                    "Principal amount %s must be between %s and %s",
                    principalAmount, product.getMinAmount(), product.getMaxAmount()));
        }

        loanEligibilityService.validateLoanRequest(customer, principalAmount);

        IdempotencyKey idempotencyKey = idempotencyKeyValue != null
                ? IdempotencyKey.of(idempotencyKeyValue)
                : IdempotencyKey.generate();

        LocalDate originationDate = LocalDate.now();
        LocalDate dueDate = calculateDueDate(originationDate, product, billingCycle, customer);

        Loan loan = Loan.builder()
                .loanNumber(UUID.randomUUID().toString())
                .customer(customer)
                .loanProduct(product)
                .principalAmount(principalAmount)
                .outstandingBalance(principalAmount)
                .billingCycle(billingCycle != null ? billingCycle : BillingCycleType.INDIVIDUAL)
                .originationDate(originationDate)
                .dueDate(dueDate)
                .idempotencyKey(idempotencyKey)
                .state(LoanState.OPEN)
                .build();

        loanRepository.save(loan);

        recordEvent(loan, LoanEventHistory.LoanEventType.LOAN_CREATED,
                "Loan created with principal: " + principalAmount, principalAmount, null, LoanState.OPEN.name());

        eventPublisher.publishEvent(new LoanCreatedEvent(this, loan.getId(), customerId, principalAmount));

        return loan;
    }

    @Transactional
    public Loan disburseLoan(Long loanId) {
        log.info("Disbursing loan: {}", loanId);

        Loan loan = findById(loanId);

        if (loan.getOriginationDate() != null && !loan.getInstallments().isEmpty()) {
            throw new BusinessRuleException("Loan already disbursed: " + loanId);
        }

        LoanScheduleGenerator generator = scheduleGeneratorFactory.resolve(loan.getLoanProduct().getLoanType());
        List<Installment> installments = generator.generateSchedule(loan);
        installments.forEach(loan::addInstallment);

        loanRepository.save(loan);

        recordEvent(loan, LoanEventHistory.LoanEventType.LOAN_DISBURSED,
                "Loan disbursed, installments generated: " + installments.size(),
                loan.getPrincipalAmount(), null, LoanState.OPEN.name());

        eventPublisher.publishEvent(new LoanDisbursedEvent(this, loan.getId(), loan.getCustomer().getId(),
                loan.getPrincipalAmount(), loan.getOriginationDate(), loan.getDueDate()));

        return loan;
    }

    @Transactional
    public Loan cancelLoan(Long loanId, String reason) {
        log.info("Cancelling loan: {}", loanId);

        Loan loan = findById(loanId);
        LoanState previousState = loan.getState();

        stateMachine.transition(loan, LoanState.CANCELLED);

        recordEvent(loan, LoanEventHistory.LoanEventType.LOAN_CANCELLED,
                "Loan cancelled. Reason: " + reason, null, previousState.name(), LoanState.CANCELLED.name());

        eventPublisher.publishEvent(new LoanCancelledEvent(this, loan.getId(),
                loan.getCustomer().getId(), loan.getPrincipalAmount(), reason));

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan markOverdue(Long loanId) {
        log.info("Marking loan overdue: {}", loanId);

        Loan loan = findById(loanId);
        if (!loan.isOpen()) {
            throw new BusinessRuleException("Only OPEN loans can be marked overdue: " + loanId);
        }

        LoanState previousState = loan.getState();
        stateMachine.transition(loan, LoanState.OVERDUE);

        loan.getInstallments().stream()
                .filter(i -> i.getStatus() == InstallmentStatus.PENDING || i.getStatus() == InstallmentStatus.PARTIAL)
                .forEach(Installment::markOverdue);

        recordEvent(loan, LoanEventHistory.LoanEventType.LOAN_OVERDUE,
                "Loan marked overdue, past due date: " + loan.getDueDate(),
                loan.getOutstandingBalance(), previousState.name(), LoanState.OVERDUE.name());

        int daysOverdue = (int) java.time.temporal.ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());

        eventPublisher.publishEvent(new LoanOverdueEvent(this, loan.getId(),
                loan.getCustomer().getId(), loan.getOutstandingBalance(), loan.getDueDate(), daysOverdue));

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan writeOffLoan(Long loanId) {
        log.info("Writing off loan: {}", loanId);

        Loan loan = findById(loanId);
        LoanState previousState = loan.getState();

        stateMachine.transition(loan, LoanState.WRITTEN_OFF);

        recordEvent(loan, LoanEventHistory.LoanEventType.LOAN_WRITTEN_OFF,
                "Loan written off. Outstanding: " + loan.getOutstandingBalance(),
                loan.getOutstandingBalance(), previousState.name(), LoanState.WRITTEN_OFF.name());

        return loanRepository.save(loan);
    }

    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    public Loan findById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", id));
    }

    public List<Loan> findByCustomerId(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    public List<Loan> findByState(LoanState state) {
        return loanRepository.findByState(state);
    }

    public List<Installment> getInstallments(Long loanId) {
        return installmentRepository.findByLoanId(loanId);
    }

    public List<LoanEventHistory> getLoanTimeline(Long loanId) {
        findById(loanId);
        return eventHistoryRepository.findByLoanIdOrderByOccurredAtDesc(loanId);
    }

    private LocalDate calculateDueDate(LocalDate originationDate, LoanProduct product,
                                       BillingCycleType billingCycle, Customer customer) {
        if (billingCycle == BillingCycleType.CONSOLIDATED) {
            BillingProfile profile = billingProfileRepository.findByCustomerId(customer.getId())
                    .orElse(null);
            if (profile != null) {
                int billingDay = profile.getBillingDay();
                LocalDate nextBillingDate = originationDate.withDayOfMonth(Math.min(billingDay, originationDate.lengthOfMonth()));
                if (!nextBillingDate.isAfter(originationDate)) {
                    nextBillingDate = nextBillingDate.plusMonths(1);
                }
                return nextBillingDate;
            }
        }

        return switch (product.getTenureConfig().getType()) {
            case DAYS -> originationDate.plusDays(product.getTenureConfig().getValue());
            case MONTHS -> originationDate.plusMonths(product.getTenureConfig().getValue());
        };
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
