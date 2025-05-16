package com.lgcns.domain.notification.service;

import com.lgcns.domain.notification.dto.response.NotificationResponse;
import java.util.List;

public interface NotificationService {

    List<NotificationResponse> findNotificationList(Long popupId);
}
