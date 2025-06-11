package com.lgcns.domain.notification.event.handler;

import com.lgcns.domain.notification.event.dto.NotificationEvent;
import com.lgcns.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class NotificationEventHandler {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        notificationService.sendLowStockMessage(event.popup(), event.item());
    }
}
