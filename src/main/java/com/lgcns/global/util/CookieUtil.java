package com.lgcns.global.util;

import static com.lgcns.global.common.constants.SecurityConstants.REFRESH_TOKEN_COOKIE_NAME;
import static com.lgcns.global.helper.SpringEnvironmentHelper.DEV;

import com.lgcns.global.helper.SpringEnvironmentHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final SpringEnvironmentHelper springEnvironmentHelper;

    public HttpHeaders generateRefreshTokenCookie(String refreshToken) {
        ResponseCookie refreshTokenCookie =
                ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                        .path("/")
                        .domain(getCookieDomain())
                        .secure(true)
                        .sameSite(Cookie.SameSite.NONE.attributeValue())
                        .httpOnly(true)
                        .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }

    public HttpHeaders deleteRefreshTokenCookie() {
        ResponseCookie refreshTokenCookie =
                ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                        .path("/")
                        .maxAge(0)
                        .domain(getCookieDomain())
                        .secure(true)
                        .sameSite(Cookie.SameSite.NONE.attributeValue())
                        .httpOnly(true)
                        .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }

    private String getCookieDomain() {
        return switch (springEnvironmentHelper.getCurrentProfile()) {
            case DEV -> ".ceo.popi.today";
            default -> "localhost";
        };
    }
}
