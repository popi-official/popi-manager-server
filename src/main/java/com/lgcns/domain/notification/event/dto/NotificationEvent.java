package com.lgcns.domain.notification.event.dto;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.popup.domain.Popup;

public record NotificationEvent(Popup popup, Item item) {
    public static NotificationEvent of(Popup popup, Item item) {
        return new NotificationEvent(popup, item);
    }
}
