package com.lending.app.product.api.dto;

import com.lending.app.product.domain.model.LoanType;
import com.lending.app.product.domain.model.TenureType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Product name is required")
        @Schema(example = "30-Day Payday Loan")
        String name,

        @Schema(example = "Short-term payday loan for immediate cash needs")
        String description,

        @NotNull(message = "Tenure value is required")
        @Positive(message = "Tenure value must be positive")
        @Schema(example = "30")
        Integer tenureValue,

        @NotNull(message = "Tenure type is required")
        @Schema(example = "DAYS")
        TenureType tenureType,

        @NotNull(message = "Minimum amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Minimum amount must be greater than 0")
        @Schema(example = "100")
        BigDecimal minAmount,

        @NotNull(message = "Maximum amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Maximum amount must be greater than 0")
        @Schema(example = "2000")
        BigDecimal maxAmount,

        @NotNull(message = "Interest rate is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Interest rate cannot be negative")
        @Schema(example = "15.00")
        BigDecimal interestRate,

        @NotNull(message = "Loan type is required")
        @Schema(example = "LUMP_SUM")
        LoanType loanType
) {
}
