package com.lgcns.domain.reservationStats.exception;

import com.lgcns.global.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReservationStatsErrorCode implements ErrorCode {
    RESERVATION_SERVICE_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "예약 서비스에 연결할 수 없습니다."),
    RESERVATION_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예약 서비스에서 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
