package com.lending.app.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
public class PaymentReceivedEvent extends ApplicationEvent {

    private final Long loanId;
    private final Long customerId;
    private final Long repaymentId;
    private final BigDecimal amount;
    private final BigDecimal remainingBalance;
    private final Instant occurredAt;

    public PaymentReceivedEvent(Object source, Long loanId, Long customerId, Long repaymentId,
                                BigDecimal amount, BigDecimal remainingBalance) {
        super(source);
        this.loanId = loanId;
        this.customerId = customerId;
        this.repaymentId = repaymentId;
        this.amount = amount;
        this.remainingBalance = remainingBalance;
        this.occurredAt = Instant.now();
    }
}
