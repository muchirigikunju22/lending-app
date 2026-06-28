package com.lending.app.loan.domain.model;

import com.lending.app.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "loan_event_history")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LoanEventHistory extends BaseEntity {

    @Column(name = "loan_id", nullable = false)
    private Long loanId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 30)
    private LoanEventType eventType;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "previous_state", length = 20)
    private String previousState;

    @Column(name = "new_state", length = 20)
    private String newState;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    public enum LoanEventType {
        LOAN_CREATED,
        LOAN_DISBURSED,
        PAYMENT_RECEIVED,
        LOAN_OVERDUE,
        LOAN_CLOSED,
        LOAN_CANCELLED,
        LOAN_WRITTEN_OFF,
        FEE_ACCRUED,
        STATE_CHANGED
    }
}
