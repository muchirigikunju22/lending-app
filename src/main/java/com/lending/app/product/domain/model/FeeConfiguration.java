package com.lending.app.product.domain.model;

import com.lending.app.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "fee_configuration")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FeeConfiguration extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private LoanProduct loanProduct;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false, length = 20)
    private FeeType feeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "calculation_method", nullable = false, length = 20)
    private CalculationMethod calculationMethod;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "days_after_due")
    private Integer daysAfterDue;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    public enum CalculationMethod {
        FIXED,
        PERCENTAGE
    }
}
