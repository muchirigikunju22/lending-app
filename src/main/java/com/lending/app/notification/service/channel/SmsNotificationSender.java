package com.lending.app.notification.service.channel;

import com.lending.app.notification.domain.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsNotificationSender implements NotificationSender {

    @Override
    public void send(Notification notification) {
        log.info("[SMS] To: customer={}, Message: {}",
                notification.getCustomerId(), notification.getBody());
    }
}
