package com.lending.app.loan.domain.model;

import com.lending.app.common.model.BaseEntity;
import com.lending.app.common.model.IdempotencyKey;
import com.lending.app.customer.domain.model.Customer;
import com.lending.app.product.domain.model.LoanProduct;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Loan extends BaseEntity {

    @Column(name = "loan_number", nullable = false, unique = true, length = 36)
    private String loanNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private LoanProduct loanProduct;

    @Column(name = "principal_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal principalAmount;

    @Column(name = "outstanding_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal outstandingBalance;

    @Column(name = "total_fees_accrued", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalFeesAccrued = BigDecimal.ZERO;

    @Column(name = "total_repaid", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalRepaid = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    @Builder.Default
    private LoanState state = LoanState.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false, length = 20)
    private BillingCycleType billingCycle;

    @Column(name = "origination_date")
    private LocalDate originationDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Embedded
    private IdempotencyKey idempotencyKey;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @OrderBy("installmentNumber ASC")
    private List<Installment> installments = new ArrayList<>();

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @OrderBy("paymentDate DESC")
    private List<Repayment> repayments = new ArrayList<>();

    public void addInstallment(Installment installment) {
        installments.add(installment);
        installment.setLoan(this);
    }

    public void addRepayment(Repayment repayment) {
        repayments.add(repayment);
        repayment.setLoan(this);
        this.totalRepaid = this.totalRepaid.add(repayment.getAmount());
        this.outstandingBalance = this.outstandingBalance.subtract(repayment.getAmount());

        if (this.outstandingBalance.compareTo(BigDecimal.ZERO) <= 0) {
            this.outstandingBalance = BigDecimal.ZERO;
        }
    }

    public void accrueFees(BigDecimal feeAmount) {
        this.totalFeesAccrued = this.totalFeesAccrued.add(feeAmount);
        this.outstandingBalance = this.outstandingBalance.add(feeAmount);
    }

    public boolean isClosed() {
        return this.state == LoanState.CLOSED;
    }

    public boolean isOpen() {
        return this.state == LoanState.OPEN;
    }

    public boolean isOverdue() {
        return this.state == LoanState.OVERDUE;
    }

    public boolean isCancelled() {
        return this.state == LoanState.CANCELLED;
    }

    public boolean isFullyPaid() {
        return this.outstandingBalance.compareTo(BigDecimal.ZERO) <= 0;
    }
}
