package com.lgcns.domain.notification.repository;

import com.lgcns.domain.notification.domain.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndPopupId(Long userId, Long popupId);
}
