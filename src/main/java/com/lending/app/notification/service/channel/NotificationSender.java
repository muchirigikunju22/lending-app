package com.lending.app.notification.service.channel;

import com.lending.app.notification.domain.model.Notification;

public interface NotificationSender {

    void send(Notification notification);
}
