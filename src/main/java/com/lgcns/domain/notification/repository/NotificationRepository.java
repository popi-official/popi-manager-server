package com.lgcns.domain.notification.repository;

import com.lgcns.domain.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository
        extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {}
