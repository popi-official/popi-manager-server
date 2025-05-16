package com.lgcns.domain.notification.repository;

import com.lgcns.domain.notification.dto.response.NotificationResponse;
import java.util.List;

public interface NotificationRepositoryCustom {

    List<NotificationResponse> findByManagerIdAndPopupId(Long managerId, Long popupId);
}
