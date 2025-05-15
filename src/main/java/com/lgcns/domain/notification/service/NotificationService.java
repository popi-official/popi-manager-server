package com.lgcns.domain.notification.service;

import com.lgcns.domain.notification.dto.NotificationResponseDTO;
import java.util.List;

public interface NotificationService {

    List<NotificationResponseDTO> findNotificationList(Long popupId);
}
