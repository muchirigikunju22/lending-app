package com.lending.app.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
public class LoanCreatedEvent extends ApplicationEvent {

    private final Long loanId;
    private final Long customerId;
    private final BigDecimal principalAmount;
    private final Instant occurredAt;

    public LoanCreatedEvent(Object source, Long loanId, Long customerId, BigDecimal principalAmount) {
        super(source);
        this.loanId = loanId;
        this.customerId = customerId;
        this.principalAmount = principalAmount;
        this.occurredAt = Instant.now();
    }
}
