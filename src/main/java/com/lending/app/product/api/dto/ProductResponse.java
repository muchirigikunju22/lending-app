package com.lending.app.product.api.dto;

import com.lending.app.product.domain.model.LoanType;
import com.lending.app.product.domain.model.TenureType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Integer tenureValue,
        TenureType tenureType,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        BigDecimal interestRate,
        LoanType loanType,
        Boolean active,
        List<FeeConfigResponse> feeConfigurations,
        Instant createdAt,
        Instant updatedAt
) {
}
