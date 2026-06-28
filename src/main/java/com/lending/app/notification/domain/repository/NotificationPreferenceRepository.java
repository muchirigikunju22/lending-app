package com.lending.app.notification.domain.repository;

import com.lending.app.notification.domain.model.NotificationChannel;
import com.lending.app.notification.domain.model.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {

    List<NotificationPreference> findByCustomerId(Long customerId);

    Optional<NotificationPreference> findByCustomerIdAndChannel(Long customerId, NotificationChannel channel);
}
