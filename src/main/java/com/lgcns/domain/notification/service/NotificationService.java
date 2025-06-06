package com.lgcns.domain.notification.service;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.notification.dto.response.NotificationResponse;
import com.lgcns.domain.popup.domain.Popup;
import java.util.List;

public interface NotificationService {

    List<NotificationResponse> findNotificationList(Long popupId);

    void sendLowStockMessage(Popup popup, Item item);
}
