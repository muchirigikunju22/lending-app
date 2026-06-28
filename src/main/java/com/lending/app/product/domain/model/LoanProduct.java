package com.lending.app.product.domain.model;

import com.lending.app.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan_product")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LoanProduct extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Embedded
    private TenureConfig tenureConfig;

    @Column(name = "min_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal minAmount;

    @Column(name = "max_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal maxAmount;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false, length = 20)
    private LoanType loanType;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "loanProduct", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<FeeConfiguration> feeConfigurations = new ArrayList<>();

    public void addFeeConfiguration(FeeConfiguration fee) {
        feeConfigurations.add(fee);
        fee.setLoanProduct(this);
    }

    public void removeFeeConfiguration(FeeConfiguration fee) {
        feeConfigurations.remove(fee);
        fee.setLoanProduct(null);
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
}
