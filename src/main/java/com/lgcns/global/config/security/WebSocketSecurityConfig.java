package com.lgcns.global.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.nullDestMatcher()
                .permitAll()
                .simpSubscribeDestMatchers("/topic/**")
                .authenticated()
                .anyMessage()
                .denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
