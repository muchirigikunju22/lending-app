package com.lending.app.customer.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LoanLimitRequest(
        @NotNull(message = "Max single loan amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Max single loan must be greater than 0")
        @Schema(example = "5000")
        BigDecimal maxSingleLoan,

        @NotNull(message = "Max total outstanding is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Max total outstanding must be greater than 0")
        @Schema(example = "15000")
        BigDecimal maxTotalOutstanding,

        @NotNull(message = "Max active loans is required")
        @Min(value = 1, message = "Max active loans must be at least 1")
        @Schema(example = "3")
        Integer maxActiveLoans
) {
}
