package com.lending.app.notification.api.dto;

import com.lending.app.notification.domain.model.NotificationChannel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record NotificationPreferenceRequest(
        @NotNull(message = "Channel is required")
        @Schema(example = "EMAIL")
        NotificationChannel channel,

        @NotNull(message = "Enabled status is required")
        @Schema(example = "true")
        Boolean enabled
) {
}
