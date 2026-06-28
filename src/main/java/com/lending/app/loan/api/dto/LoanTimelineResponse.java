package com.lending.app.loan.api.dto;

import com.lending.app.loan.domain.model.LoanEventHistory;

import java.math.BigDecimal;
import java.time.Instant;

public record LoanTimelineResponse(
        Long id,
        LoanEventHistory.LoanEventType eventType,
        String description,
        BigDecimal amount,
        String previousState,
        String newState,
        Instant occurredAt
) {
}
