package com.lending.app.loan.api.dto;

import com.lending.app.loan.domain.model.InstallmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InstallmentResponse(
        Long id,
        Integer installmentNumber,
        BigDecimal amountDue,
        BigDecimal amountPaid,
        BigDecimal remainingAmount,
        LocalDate dueDate,
        InstallmentStatus status
) {
}
