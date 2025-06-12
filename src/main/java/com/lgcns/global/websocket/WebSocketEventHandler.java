package com.lgcns.global.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Slf4j
@Component
public class WebSocketEventHandler {

    @EventListener(SessionSubscribeEvent.class)
    public void handleWebSocketSessionSubscribe() {
        log.info("WebSocket Subscribe");
    }

    @EventListener(SessionUnsubscribeEvent.class)
    public void handleWebSocketSessionUnsubscribe() {
        log.info("WebSocket Unsubscribe");
    }
}
