package com.lending.app.loan.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RepaymentRequest(
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
        @Schema(example = "500")
        BigDecimal amount,

        @Schema(example = "BANK_TRANSFER")
        String paymentMethod,

        @Schema(example = "1")
        Long installmentId
) {
}
