package com.lending.app.loan.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record RepaymentResponse(
        Long id,
        Long loanId,
        Long installmentId,
        BigDecimal amount,
        String paymentMethod,
        String transactionReference,
        Instant paymentDate
) {
}
