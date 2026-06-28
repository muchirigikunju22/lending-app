package com.lending.app.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
public class LoanClosedEvent extends ApplicationEvent {

    private final Long loanId;
    private final Long customerId;
    private final BigDecimal totalPaid;
    private final String closureReason;
    private final Instant occurredAt;

    public LoanClosedEvent(Object source, Long loanId, Long customerId, BigDecimal totalPaid, String closureReason) {
        super(source);
        this.loanId = loanId;
        this.customerId = customerId;
        this.totalPaid = totalPaid;
        this.closureReason = closureReason;
        this.occurredAt = Instant.now();
    }
}
