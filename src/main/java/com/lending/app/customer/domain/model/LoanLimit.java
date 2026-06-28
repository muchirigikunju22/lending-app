package com.lending.app.customer.domain.model;

import com.lending.app.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "loan_limit")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LoanLimit extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    @Column(name = "max_single_loan", nullable = false, precision = 15, scale = 2)
    private BigDecimal maxSingleLoan;

    @Column(name = "max_total_outstanding", nullable = false, precision = 15, scale = 2)
    private BigDecimal maxTotalOutstanding;

    @Column(name = "max_active_loans", nullable = false)
    private Integer maxActiveLoans;

    @Column(name = "last_reviewed")
    private Instant lastReviewed;
}
