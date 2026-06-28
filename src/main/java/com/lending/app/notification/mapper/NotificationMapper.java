package com.lending.app.notification.mapper;

import com.lending.app.notification.api.dto.NotificationResponse;
import com.lending.app.notification.domain.model.Notification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toResponse(Notification notification);

    List<NotificationResponse> toResponseList(List<Notification> notifications);
}
