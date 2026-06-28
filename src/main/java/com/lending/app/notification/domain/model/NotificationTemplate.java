package com.lending.app.notification.domain.model;

import com.lending.app.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "notification_template")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate extends BaseEntity {

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    @Column(name = "subject_template", length = 200)
    private String subjectTemplate;

    @Column(name = "body_template", nullable = false, length = 2000)
    private String bodyTemplate;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;
}
