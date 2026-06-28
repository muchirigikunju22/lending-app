package com.lending.app.notification.service.channel;

import com.lending.app.notification.domain.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailNotificationSender implements NotificationSender {

    @Override
    public void send(Notification notification) {
        log.info("[EMAIL] To: customer={}, Subject: {}, Body: {}",
                notification.getCustomerId(), notification.getSubject(), notification.getBody());
    }
}
