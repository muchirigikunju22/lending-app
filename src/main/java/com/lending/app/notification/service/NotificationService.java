package com.lending.app.notification.service;

import com.lending.app.common.exception.ResourceNotFoundException;
import com.lending.app.customer.domain.model.Customer;
import com.lending.app.customer.domain.repository.CustomerRepository;
import com.lending.app.notification.domain.model.*;
import com.lending.app.notification.domain.repository.NotificationPreferenceRepository;
import com.lending.app.notification.domain.repository.NotificationRepository;
import com.lending.app.notification.domain.repository.NotificationTemplateRepository;
import com.lending.app.notification.service.channel.NotificationSender;
import com.lending.app.notification.service.channel.NotificationSenderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationSenderFactory senderFactory;
    private final CustomerRepository customerRepository;

    @Transactional
    public Notification sendNotification(Long customerId, Long loanId, String eventType,
                                          Map<String, String> variables, NotificationChannel channel) {
        NotificationTemplate template = templateRepository
                .findByEventTypeAndChannelAndActiveTrue(eventType, channel)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NotificationTemplate", eventType + "/" + channel));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        String subject = renderTemplate(template.getSubjectTemplate(), variables);
        String body = renderTemplate(template.getBodyTemplate(), variables);

        Notification notification = Notification.builder()
                .customerId(customerId)
                .loanId(loanId)
                .channel(channel)
                .subject(subject)
                .body(body)
                .eventType(eventType)
                .status(NotificationStatus.PENDING)
                .build();

        notificationRepository.save(notification);

        try {
            NotificationSender sender = senderFactory.resolve(channel);
            sender.send(notification);

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(Instant.now());
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
        }

        return notificationRepository.save(notification);
    }

    @Transactional
    public void processEventForAllChannels(Long customerId, Long loanId, String eventType,
                                            Map<String, String> variables) {
        List<NotificationTemplate> templates = templateRepository.findByEventTypeAndActiveTrue(eventType);

        if (templates.isEmpty()) {
            log.warn("No templates found for event type: {}", eventType);
            return;
        }

        List<NotificationPreference> preferences = preferenceRepository.findByCustomerId(customerId);

        for (NotificationTemplate template : templates) {
            boolean isEnabled = preferences.stream()
                    .filter(p -> p.getChannel() == template.getChannel())
                    .findFirst()
                    .map(NotificationPreference::getEnabled)
                    .orElse(true);

            if (isEnabled) {
                try {
                    sendNotification(customerId, loanId, eventType, variables, template.getChannel());
                } catch (Exception e) {
                    log.error("Failed to process notification for channel {}: {}", template.getChannel(), e.getMessage());
                }
            }
        }
    }

    public List<Notification> getCustomerNotifications(Long customerId) {
        return notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Transactional
    public NotificationPreference setPreference(Long customerId, NotificationChannel channel, boolean enabled) {
        NotificationPreference preference = preferenceRepository
                .findByCustomerIdAndChannel(customerId, channel)
                .orElseGet(() -> {
                    Customer customer = customerRepository.findById(customerId)
                            .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));
                    NotificationPreference newPref = NotificationPreference.builder()
                            .customer(customer)
                            .channel(channel)
                            .build();
                    return preferenceRepository.save(newPref);
                });

        preference.setEnabled(enabled);
        return preferenceRepository.save(preference);
    }

    public List<NotificationPreference> getCustomerPreferences(Long customerId) {
        return preferenceRepository.findByCustomerId(customerId);
    }

    private String renderTemplate(String template, Map<String, String> variables) {
        if (template == null) return null;
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}
