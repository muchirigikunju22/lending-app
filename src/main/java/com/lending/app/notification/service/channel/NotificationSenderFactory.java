package com.lending.app.notification.service.channel;

import com.lending.app.notification.domain.model.NotificationChannel;
import org.springframework.stereotype.Component;

@Component
public class NotificationSenderFactory {

    private final EmailNotificationSender emailSender;
    private final SmsNotificationSender smsSender;
    private final PushNotificationSender pushSender;

    public NotificationSenderFactory(EmailNotificationSender emailSender,
                                      SmsNotificationSender smsSender,
                                      PushNotificationSender pushSender) {
        this.emailSender = emailSender;
        this.smsSender = smsSender;
        this.pushSender = pushSender;
    }

    public NotificationSender resolve(NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> emailSender;
            case SMS -> smsSender;
            case PUSH -> pushSender;
        };
    }
}
