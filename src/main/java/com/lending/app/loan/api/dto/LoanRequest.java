package com.lending.app.loan.api.dto;

import com.lending.app.loan.domain.model.BillingCycleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LoanRequest(
        @NotNull(message = "Customer ID is required")
        @Schema(example = "1")
        Long customerId,

        @NotNull(message = "Product ID is required")
        @Schema(example = "1")
        Long productId,

        @NotNull(message = "Principal amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Principal amount must be greater than 0")
        @Schema(example = "1000")
        BigDecimal principalAmount,

        @Schema(description = "Billing cycle type: INDIVIDUAL or CONSOLIDATED", example = "INDIVIDUAL")
        BillingCycleType billingCycle
) {
}
