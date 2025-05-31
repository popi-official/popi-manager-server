package com.lgcns.domain.visitorStats.exception;

import com.lgcns.global.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum VisitorStatsErrorCode implements ErrorCode {
    HOURLY_ENTRANCE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "이전 시간 입장 정보가 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
