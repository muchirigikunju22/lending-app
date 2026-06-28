package com.lending.app.customer.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record LoanLimitResponse(
        Long id,
        BigDecimal maxSingleLoan,
        BigDecimal maxTotalOutstanding,
        Integer maxActiveLoans,
        Instant lastReviewed
) {
}
