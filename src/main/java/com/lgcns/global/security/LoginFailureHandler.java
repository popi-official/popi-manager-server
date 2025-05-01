package com.lgcns.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgcns.domain.auth.exception.AuthErrorCode;
import com.lgcns.global.common.response.GlobalResponse;
import com.lgcns.global.error.ErrorResponse;
import com.lgcns.global.error.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {

        log.error("로그인 실패: {}", exception.getMessage(), exception);
        // 1. 사용할 에러 코드 정의
        final ErrorCode errorCode =
                AuthErrorCode.AUTHENTICATION_FAILED; // 로그인 실패에 해당하는 ErrorCode 사용

        final ErrorResponse errorResponse =
                ErrorResponse.of(errorCode.getErrorName(), errorCode.getMessage());

        final GlobalResponse globalResponse =
                GlobalResponse.fail(errorCode.getHttpStatus().value(), errorResponse);

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getWriter(), globalResponse);
    }
}
