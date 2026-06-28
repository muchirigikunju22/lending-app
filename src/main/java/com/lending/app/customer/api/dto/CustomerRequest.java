package com.lending.app.customer.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CustomerRequest(
        @NotBlank(message = "First name is required")
        @Schema(example = "John")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Schema(example = "Smith")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Schema(example = "james.mwangi@email.com")
        String email,

        @Schema(example = "0720334412")
        String phone,

        @Schema(example = "41212234")
        String nationalId,

        @Schema(example = "750.00")
        BigDecimal creditScore,

        @Past(message = "Date of birth must be in the past")
        @Schema(example = "1985-03-15")
        LocalDate dateOfBirth,

        @Schema(example = "123 Kimathi St, Nairobi")
        String address,

        @Schema(example = "5000.00")
        BigDecimal maxSingleLoan,

        @Schema(example = "15000.00")
        BigDecimal maxTotalOutstanding,

        @Schema(example = "3")
        Integer maxActiveLoans
) {
}
