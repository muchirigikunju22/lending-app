package com.lending.app.customer.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BillingProfileRequest(
        @NotNull(message = "Billing day is required")
        @Min(value = 1, message = "Billing day must be between 1 and 31")
        @Max(value = 31, message = "Billing day must be between 1 and 31")
        @Schema(example = "25")
        Integer billingDay
) {
}
