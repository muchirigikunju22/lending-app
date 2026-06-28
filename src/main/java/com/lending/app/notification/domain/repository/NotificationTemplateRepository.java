package com.lending.app.notification.domain.repository;

import com.lending.app.notification.domain.model.NotificationChannel;
import com.lending.app.notification.domain.model.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    List<NotificationTemplate> findByEventTypeAndActiveTrue(String eventType);

    Optional<NotificationTemplate> findByEventTypeAndChannelAndActiveTrue(String eventType, NotificationChannel channel);
}
