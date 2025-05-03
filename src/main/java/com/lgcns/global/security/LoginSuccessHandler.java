package com.lgcns.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgcns.domain.auth.dto.response.LoginResponse;
import com.lgcns.domain.auth.service.JwtTokenService;
import com.lgcns.domain.manager.domain.ManagerRole;
import com.lgcns.global.common.response.GlobalResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private static final String RESPONSE_CONTENT_TYPE = "application/json;charset=utf-8";
    private final ObjectMapper objectMapper;
    private final JwtTokenService jwtTokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        response.setContentType(RESPONSE_CONTENT_TYPE);

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        Long managerId = Long.parseLong(principalDetails.getUsername());
        ManagerRole managerRole =
                ManagerRole.valueOf(
                        principalDetails.getAuthorities().iterator().next().getAuthority());

        String accessToken = jwtTokenService.createAccessToken(managerId, managerRole);
        String refreshToken = jwtTokenService.createRefreshToken(managerId);

        LoginResponse loginResponse = LoginResponse.of(accessToken, refreshToken);

        GlobalResponse<LoginResponse> globalResponse =
                GlobalResponse.success(HttpServletResponse.SC_OK, loginResponse);

        objectMapper.writeValue(response.getWriter(), globalResponse);
    }
}
