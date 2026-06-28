package com.lending.app.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
public class LoanDisbursedEvent extends ApplicationEvent {

    private final Long loanId;
    private final Long customerId;
    private final BigDecimal disbursedAmount;
    private final LocalDate disbursementDate;
    private final LocalDate dueDate;
    private final Instant occurredAt;

    public LoanDisbursedEvent(Object source, Long loanId, Long customerId, BigDecimal disbursedAmount,
                              LocalDate disbursementDate, LocalDate dueDate) {
        super(source);
        this.loanId = loanId;
        this.customerId = customerId;
        this.disbursedAmount = disbursedAmount;
        this.disbursementDate = disbursementDate;
        this.dueDate = dueDate;
        this.occurredAt = Instant.now();
    }
}
