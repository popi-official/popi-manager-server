package com.lgcns.domain.auth.externalApi;

import static com.lgcns.global.common.constants.SecurityConstants.REFRESH_TOKEN_COOKIE_NAME;

import com.lgcns.domain.auth.dto.response.TokenReissueResponse;
import com.lgcns.domain.auth.service.AuthService;
import com.lgcns.global.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "1-1. 인증 API", description = "인증 관련 API입니다.")
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @Operation(
            summary = "토큰 재발급",
            description = "만료된 엑세스 토큰이 있을 경우, 리프레시 토큰을 이용해 엑세스 및 리프레시 토큰을 재발급합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<TokenReissueResponse> tokenReissue(
            @CookieValue(REFRESH_TOKEN_COOKIE_NAME) String refreshTokenValue) {
        TokenReissueResponse response = authService.reissueToken(refreshTokenValue);

        String refreshToken = response.refreshToken();
        HttpHeaders headers = cookieUtil.generateRefreshTokenCookie(refreshToken);

        return ResponseEntity.ok().headers(headers).body(response);
    }
}
