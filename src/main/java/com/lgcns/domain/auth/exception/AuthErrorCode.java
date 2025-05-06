package com.lgcns.domain.auth.exception;

import com.lgcns.global.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다."),

    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "유효한 리프레시 토큰이 존재하지 않습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다. 다시 로그인해주세요."),

    AUTH_NOT_FOUND(HttpStatus.UNAUTHORIZED, "사용자 인증 정보를 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
