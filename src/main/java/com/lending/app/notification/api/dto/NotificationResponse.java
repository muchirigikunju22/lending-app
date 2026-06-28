package com.lending.app.notification.api.dto;

import com.lending.app.notification.domain.model.NotificationChannel;
import com.lending.app.notification.domain.model.NotificationStatus;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        Long customerId,
        Long loanId,
        NotificationChannel channel,
        String subject,
        String body,
        NotificationStatus status,
        String eventType,
        Instant sentAt,
        Instant createdAt
) {
}
