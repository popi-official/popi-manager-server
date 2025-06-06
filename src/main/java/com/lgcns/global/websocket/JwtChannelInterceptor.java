package com.lgcns.global.websocket;

import static com.lgcns.global.common.constants.SecurityConstants.TOKEN_PREFIX;

import com.lgcns.domain.auth.dto.AccessTokenDto;
import com.lgcns.domain.auth.service.JwtTokenService;
import com.lgcns.domain.manager.domain.ManagerRole;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenService jwtTokenService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        String accessTokenHeaderValue = extractAccessTokenFromHeader(accessor);

        if (accessTokenHeaderValue != null) {
            AccessTokenDto accessTokenDto =
                    jwtTokenService.retrieveAccessToken(accessTokenHeaderValue);

            if (accessTokenDto != null) {
                setAuthenticationToken(accessTokenDto.managerId(), accessTokenDto.role(), accessor);
            }
        }

        return message;
    }

    private void setAuthenticationToken(
            Long managerId, ManagerRole managerRole, StompHeaderAccessor accessor) {
        UserDetails userDetails =
                User.withUsername(managerId.toString())
                        .password("")
                        .authorities(managerRole.toString())
                        .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        accessor.setUser(authentication);
    }

    private String extractAccessTokenFromHeader(StompHeaderAccessor accessor) {
        String header = accessor.getFirstNativeHeader("Authorization");

        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            return header.replace(TOKEN_PREFIX, "");
        }
        return null;
    }
}
