package com.lending.app.loan.service;

import com.lending.app.common.exception.BusinessRuleException;
import com.lending.app.customer.domain.model.Customer;
import com.lending.app.customer.domain.repository.CustomerRepository;
import com.lending.app.customer.service.LoanEligibilityService;
import com.lending.app.loan.domain.model.*;
import com.lending.app.loan.domain.repository.*;
import com.lending.app.loan.service.schedule.LoanScheduleGenerator;
import com.lending.app.loan.service.schedule.ScheduleGeneratorFactory;
import com.lending.app.loan.service.statemachine.LoanStateMachine;
import com.lending.app.product.domain.model.*;
import com.lending.app.product.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private InstallmentRepository installmentRepository;
    @Mock
    private RepaymentRepository repaymentRepository;
    @Mock
    private LoanEventHistoryRepository eventHistoryRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private LoanEligibilityService loanEligibilityService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private LoanStateMachine stateMachine;
    @Mock
    private ScheduleGeneratorFactory scheduleGeneratorFactory;
    @Mock
    private LoanScheduleGenerator loanScheduleGenerator;

    @InjectMocks
    private LoanService loanService;

    @Test
    void shouldCreateLoan() {
        Long customerId = 1L;
        Long productId = 1L;
        BigDecimal amount = new BigDecimal("1000");

        Customer customer = Customer.builder().id(customerId).firstName("John").build();
        LoanProduct product = LoanProduct.builder()
                .id(productId)
                .name("Test Product")
                .minAmount(new BigDecimal("100"))
                .maxAmount(new BigDecimal("5000"))
                .tenureConfig(TenureConfig.of(30, TenureType.DAYS))
                .active(true)
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(loanRepository.save(any())).thenAnswer(inv -> {
            Loan l = inv.getArgument(0);
            l.setId(1L);
            return l;
        });

        Loan result = loanService.createLoan(customerId, productId, amount, BillingCycleType.INDIVIDUAL, null);

        assertNotNull(result);
        assertEquals(amount, result.getPrincipalAmount());
        assertEquals(LoanState.OPEN, result.getState());
        verify(loanRepository).save(any());
    }

    @Test
    void shouldRejectLoanBelowMinimum() {
        Long customerId = 1L;
        Long productId = 1L;

        Customer customer = Customer.builder().id(customerId).build();
        LoanProduct product = LoanProduct.builder()
                .id(productId)
                .name("Test")
                .minAmount(new BigDecimal("500"))
                .maxAmount(new BigDecimal("5000"))
                .active(true)
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(BusinessRuleException.class,
                () -> loanService.createLoan(customerId, productId, new BigDecimal("100"), BillingCycleType.INDIVIDUAL, null));
    }

    @Test
    void shouldDisburseLoan() {
        Long loanId = 1L;
        Loan loan = createTestLoan(loanId, LoanState.OPEN, new BigDecimal("1000"));
        loan.setOriginationDate(java.time.LocalDate.now());

        Installment installment = Installment.builder()
                .loan(loan)
                .installmentNumber(1)
                .amountDue(new BigDecimal("1000"))
                .dueDate(loan.getDueDate())
                .build();

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(loan);
        when(scheduleGeneratorFactory.resolve(LoanType.LUMP_SUM)).thenReturn(loanScheduleGenerator);
        when(loanScheduleGenerator.generateSchedule(loan)).thenReturn(List.of(installment));

        Loan result = loanService.disburseLoan(loanId);

        assertNotNull(result);
        assertFalse(result.getInstallments().isEmpty());
    }

    @Test
    void shouldCancelLoan() {
        Long loanId = 1L;
        Loan loan = createTestLoan(loanId, LoanState.OPEN, new BigDecimal("1000"));
        loan.setTotalRepaid(BigDecimal.ZERO);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(loan);
        doAnswer(inv -> {
            Loan l = inv.getArgument(0);
            LoanState target = inv.getArgument(1);
            l.setState(target);
            return null;
        }).when(stateMachine).transition(any(Loan.class), any(LoanState.class));

        Loan result = loanService.cancelLoan(loanId, "Customer request");

        assertEquals(LoanState.CANCELLED, result.getState());
    }

    private Loan createTestLoan(Long id, LoanState state, BigDecimal outstandingBalance) {
        Loan loan = Loan.builder()
                .customer(Customer.builder().id(1L).build())
                .loanProduct(LoanProduct.builder()
                        .id(1L)
                        .loanType(LoanType.LUMP_SUM)
                        .tenureConfig(TenureConfig.of(30, TenureType.DAYS))
                        .build())
                .principalAmount(outstandingBalance)
                .outstandingBalance(outstandingBalance)
                .state(state)
                .billingCycle(BillingCycleType.INDIVIDUAL)
                .loanNumber("TEST-" + id)
                .build();
        loan.setId(id);
        loan.setInstallments(new java.util.ArrayList<>());
        loan.setRepayments(new java.util.ArrayList<>());
        return loan;
    }
}
