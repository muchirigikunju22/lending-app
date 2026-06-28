package com.lending.app.notification.domain.model;

import com.lending.app.common.model.BaseEntity;
import com.lending.app.customer.domain.model.Customer;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "notification_preference")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;
}
