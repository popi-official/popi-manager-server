package com.lgcns.global.config.websocket;

import static com.lgcns.global.common.constants.UrlConstants.DEV_CLIENT_URL;
import static com.lgcns.global.common.constants.UrlConstants.LOCAL_CLIENT_URL;
import static com.lgcns.global.helper.SpringEnvironmentHelper.DEV;

import com.lgcns.global.helper.SpringEnvironmentHelper;
import com.lgcns.global.websocket.JwtChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final SpringEnvironmentHelper springEnvironmentHelper;
    private final JwtChannelInterceptor jwtChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        var endpoint = registry.addEndpoint("/ws");

        switch (springEnvironmentHelper.getCurrentProfile()) {
            case DEV -> endpoint.setAllowedOriginPatterns(DEV_CLIENT_URL, LOCAL_CLIENT_URL);
            default -> endpoint.setAllowedOriginPatterns(LOCAL_CLIENT_URL);
        }

        endpoint.withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor);
    }
}
