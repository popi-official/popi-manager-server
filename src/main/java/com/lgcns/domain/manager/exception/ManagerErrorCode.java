package com.lgcns.domain.manager.exception;

import com.lgcns.global.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ManagerErrorCode implements ErrorCode {
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 사용 중인 사용자 이름입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String getErrorName() {
        return this.name();
    }
}
