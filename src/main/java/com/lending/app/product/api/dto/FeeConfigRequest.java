package com.lending.app.product.api.dto;

import com.lending.app.product.domain.model.FeeConfiguration;
import com.lending.app.product.domain.model.FeeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record FeeConfigRequest(
        @NotNull(message = "Fee type is required")
        @Schema(example = "SERVICE")
        FeeType feeType,

        @NotNull(message = "Calculation method is required")
        @Schema(example = "FIXED")
        FeeConfiguration.CalculationMethod calculationMethod,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
        @Schema(example = "25.00")
        BigDecimal amount,

        @Schema(example = "3")
        Integer daysAfterDue
) {
}
