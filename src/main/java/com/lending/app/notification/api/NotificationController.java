package com.lending.app.notification.api;

import com.lending.app.notification.api.dto.NotificationPreferenceRequest;
import com.lending.app.notification.api.dto.NotificationResponse;
import com.lending.app.notification.domain.model.Notification;
import com.lending.app.notification.domain.model.NotificationPreference;
import com.lending.app.notification.mapper.NotificationMapper;
import com.lending.app.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management APIs")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @GetMapping("/api/v1/customers/{customerId}/notifications")
    @Operation(summary = "Get customer notification history")
    public ResponseEntity<List<NotificationResponse>> getCustomerNotifications(@PathVariable @Parameter(example = "1") Long customerId) {
        List<Notification> notifications = notificationService.getCustomerNotifications(customerId);
        return ResponseEntity.ok(notificationMapper.toResponseList(notifications));
    }

    @PutMapping("/api/v1/customers/{customerId}/notification-preferences")
    @Operation(summary = "Update notification preference")
    public ResponseEntity<String> setPreference(@PathVariable @Parameter(example = "1") Long customerId,
                                                 @Valid @RequestBody NotificationPreferenceRequest request) {
        notificationService.setPreference(customerId, request.channel(), request.enabled());
        return ResponseEntity.ok("Preference updated");
    }

    @GetMapping("/api/v1/customers/{customerId}/notification-preferences")
    @Operation(summary = "Get customer notification preferences")
    public ResponseEntity<List<NotificationPreference>> getPreferences(@PathVariable @Parameter(example = "1") Long customerId) {
        List<NotificationPreference> preferences = notificationService.getCustomerPreferences(customerId);
        return ResponseEntity.ok(preferences);
    }
}
