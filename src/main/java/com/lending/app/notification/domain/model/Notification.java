package com.lending.app.notification.domain.model;

import com.lending.app.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "notification")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "loan_id")
    private Long loanId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    @Column(name = "subject", length = 200)
    private String subject;

    @Column(name = "body", length = 2000)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "event_type", length = 50)
    private String eventType;

    @Column(name = "sent_at")
    private Instant sentAt;
}
