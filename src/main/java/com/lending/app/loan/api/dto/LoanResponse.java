package com.lending.app.loan.api.dto;

import com.lending.app.loan.domain.model.BillingCycleType;
import com.lending.app.loan.domain.model.LoanState;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record LoanResponse(
        Long id,
        String loanNumber,
        Long customerId,
        String customerName,
        Long productId,
        String productName,
        BigDecimal principalAmount,
        BigDecimal outstandingBalance,
        BigDecimal totalFeesAccrued,
        BigDecimal totalRepaid,
        LoanState state,
        BillingCycleType billingCycle,
        LocalDate originationDate,
        LocalDate dueDate,
        String idempotencyKey,
        List<InstallmentResponse> installments,
        Instant createdAt,
        Instant updatedAt
) {
}
