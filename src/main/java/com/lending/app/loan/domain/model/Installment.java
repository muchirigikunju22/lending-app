package com.lending.app.loan.domain.model;

import com.lending.app.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "installment")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Installment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;

    @Column(name = "amount_due", nullable = false, precision = 15, scale = 2)
    private BigDecimal amountDue;

    @Column(name = "amount_paid", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private InstallmentStatus status = InstallmentStatus.PENDING;

    public BigDecimal getRemainingAmount() {
        return amountDue.subtract(amountPaid);
    }

    public boolean isFullyPaid() {
        return amountPaid.compareTo(amountDue) >= 0;
    }

    public void addPayment(BigDecimal amount) {
        this.amountPaid = this.amountPaid.add(amount);
        updateStatus();
    }

    public void updateStatus() {
        if (isFullyPaid()) {
            this.status = InstallmentStatus.PAID;
        } else if (amountPaid.compareTo(BigDecimal.ZERO) > 0) {
            this.status = InstallmentStatus.PARTIAL;
        } else {
            this.status = InstallmentStatus.PENDING;
        }
    }

    public void markOverdue() {
        if (this.status == InstallmentStatus.PENDING || this.status == InstallmentStatus.PARTIAL) {
            this.status = InstallmentStatus.OVERDUE;
        }
    }
}
