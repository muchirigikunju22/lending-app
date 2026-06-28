package com.lending.app.loan;

import com.lending.app.customer.domain.model.BillingProfile;
import com.lending.app.customer.domain.model.Customer;
import com.lending.app.customer.domain.model.LoanLimit;
import com.lending.app.customer.domain.repository.CustomerRepository;
import com.lending.app.loan.domain.model.*;
import com.lending.app.loan.domain.repository.*;
import com.lending.app.loan.service.LoanService;
import com.lending.app.loan.service.RepaymentService;
import com.lending.app.product.domain.model.*;
import com.lending.app.product.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LoanLifecycleIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private RepaymentRepository repaymentRepository;

    @Autowired
    private LoanEventHistoryRepository eventHistoryRepository;

    @Autowired
    private LoanService loanService;

    @Autowired
    private RepaymentService repaymentService;

    @Test
    void fullLoanLifecycle() {
        // 1. Create Customer
        Customer customer = Customer.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@test.com")
                .phone("555-9999")
                .nationalId("ID999")
                .creditScore(new BigDecimal("800"))
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .active(true)
                .build();

        LoanLimit limit = LoanLimit.builder()
                .customer(customer)
                .maxSingleLoan(new BigDecimal("10000"))
                .maxTotalOutstanding(new BigDecimal("30000"))
                .maxActiveLoans(3)
                .lastReviewed(Instant.now())
                .build();
        customer.setLoanLimit(limit);

        BillingProfile profile = BillingProfile.builder()
                .customer(customer)
                .billingDay(25)
                .build();
        customer.setBillingProfile(profile);

        customer = customerRepository.save(customer);
        assertNotNull(customer.getId());

        // 2. Create Product
        LoanProduct product = LoanProduct.builder()
                .name("Integration Test Product")
                .description("For integration testing")
                .tenureConfig(TenureConfig.of(3, TenureType.MONTHS))
                .minAmount(new BigDecimal("100"))
                .maxAmount(new BigDecimal("5000"))
                .interestRate(new BigDecimal("10"))
                .loanType(LoanType.INSTALLMENT)
                .active(true)
                .build();

        FeeConfiguration serviceFee = FeeConfiguration.builder()
                .loanProduct(product)
                .feeType(FeeType.SERVICE)
                .calculationMethod(FeeConfiguration.CalculationMethod.FIXED)
                .amount(new BigDecimal("25"))
                .active(true)
                .build();
        product.addFeeConfiguration(serviceFee);

        product = productRepository.save(product);
        assertNotNull(product.getId());

        // 3. Create Loan
        Loan loan = loanService.createLoan(
                customer.getId(),
                product.getId(),
                new BigDecimal("3000"),
                BillingCycleType.INDIVIDUAL,
                null);

        assertNotNull(loan.getId());
        assertEquals(LoanState.OPEN, loan.getState());
        assertEquals(new BigDecimal("3000"), loan.getPrincipalAmount());

        // 4. Disburse Loan
        Loan disbursedLoan = loanService.disburseLoan(loan.getId());

        assertFalse(disbursedLoan.getInstallments().isEmpty());
        assertEquals(3, disbursedLoan.getInstallments().size());

        // 5. Make Partial Payment
        Repayment repayment1 = repaymentService.makePayment(
                loan.getId(), new BigDecimal("1000"), "BANK_TRANSFER");

        assertNotNull(repayment1);
        assertEquals(new BigDecimal("1000"), repayment1.getAmount());

        Loan loanAfterPayment = loanRepository.findById(loan.getId()).orElseThrow();
        assertEquals(new BigDecimal("2000"), loanAfterPayment.getOutstandingBalance());

        // 6. Make Final Payment to Close Loan
        Repayment repayment2 = repaymentService.makePayment(
                loan.getId(), new BigDecimal("2000"), "BANK_TRANSFER");

        assertNotNull(repayment2);

        Loan closedLoan = loanRepository.findById(loan.getId()).orElseThrow();
        assertEquals(LoanState.CLOSED, closedLoan.getState());
        assertEquals(BigDecimal.ZERO, closedLoan.getOutstandingBalance());

        // 7. Verify Timeline
        List<LoanEventHistory> timeline = loanService.getLoanTimeline(loan.getId());
        assertFalse(timeline.isEmpty());

        boolean hasCreated = timeline.stream()
                .anyMatch(e -> e.getEventType() == LoanEventHistory.LoanEventType.LOAN_CREATED);
        boolean hasDisbursed = timeline.stream()
                .anyMatch(e -> e.getEventType() == LoanEventHistory.LoanEventType.LOAN_DISBURSED);
        boolean hasPayment = timeline.stream()
                .anyMatch(e -> e.getEventType() == LoanEventHistory.LoanEventType.PAYMENT_RECEIVED);
        boolean hasClosed = timeline.stream()
                .anyMatch(e -> e.getEventType() == LoanEventHistory.LoanEventType.LOAN_CLOSED);

        assertTrue(hasCreated, "Should have LOAN_CREATED event");
        assertTrue(hasDisbursed, "Should have LOAN_DISBURSED event");
        assertTrue(hasPayment, "Should have PAYMENT_RECEIVED event");
        assertTrue(hasClosed, "Should have LOAN_CLOSED event");

        // 8. Verify Repayments
        List<Repayment> repayments = repaymentRepository.findByLoanId(loan.getId());
        assertEquals(2, repayments.size());
    }
}
