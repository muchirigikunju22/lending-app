package com.lending.app.product.api.dto;

import com.lending.app.product.domain.model.FeeConfiguration;
import com.lending.app.product.domain.model.FeeType;

import java.math.BigDecimal;

public record FeeConfigResponse(
        Long id,
        FeeType feeType,
        FeeConfiguration.CalculationMethod calculationMethod,
        BigDecimal amount,
        Integer daysAfterDue,
        Boolean active
) {
}
