package com.lending.app.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
public class LoanOverdueEvent extends ApplicationEvent {

    private final Long loanId;
    private final Long customerId;
    private final BigDecimal outstandingBalance;
    private final LocalDate dueDate;
    private final Integer daysOverdue;
    private final Instant occurredAt;

    public LoanOverdueEvent(Object source, Long loanId, Long customerId, BigDecimal outstandingBalance,
                            LocalDate dueDate, Integer daysOverdue) {
        super(source);
        this.loanId = loanId;
        this.customerId = customerId;
        this.outstandingBalance = outstandingBalance;
        this.dueDate = dueDate;
        this.daysOverdue = daysOverdue;
        this.occurredAt = Instant.now();
    }
}
