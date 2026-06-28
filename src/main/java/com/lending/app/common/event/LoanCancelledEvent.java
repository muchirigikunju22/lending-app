package com.lending.app.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
public class LoanCancelledEvent extends ApplicationEvent {

    private final Long loanId;
    private final Long customerId;
    private final String reason;
    private final Instant occurredAt;

    public LoanCancelledEvent(Object source, Long loanId, Long customerId, BigDecimal principalAmount, String reason) {
        super(source);
        this.loanId = loanId;
        this.customerId = customerId;
        this.reason = reason;
        this.occurredAt = Instant.now();
    }
}
