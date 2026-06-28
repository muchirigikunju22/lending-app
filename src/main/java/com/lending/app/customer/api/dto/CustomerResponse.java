package com.lending.app.customer.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record CustomerResponse(
        Long id,
        String firstName,
        String lastName,
        String fullName,
        String email,
        String phone,
        String nationalId,
        BigDecimal creditScore,
        LocalDate dateOfBirth,
        String address,
        Boolean active,
        LoanLimitResponse loanLimit,
        BillingProfileResponse billingProfile,
        Instant createdAt,
        Instant updatedAt
) {
}
