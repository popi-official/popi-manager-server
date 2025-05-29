package com.lgcns.domain.visitorStats.exception;

import com.lgcns.global.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum VisitorStatsErrorCode implements ErrorCode {
    VISITOR_STATS_DUPLICATED(HttpStatus.BAD_REQUEST, "해당 시간에 예약자 분석이 존재합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
