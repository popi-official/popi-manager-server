package com.lgcns.domain.paymentStats.exception;

import com.lgcns.global.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PaymentStatsErrorCode implements ErrorCode {
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "유효하지 않은 날짜 범위입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
