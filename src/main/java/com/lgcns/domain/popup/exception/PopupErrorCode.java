package com.lgcns.domain.popup.exception;

import com.lgcns.global.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PopupErrorCode implements ErrorCode {
    POPUP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 팝업이 존재하지 않습니다."),
    POPUP_UNAUTHORIZED(HttpStatus.FORBIDDEN, "해당 팝업의 소유자가 아닙니다."),
    PARTIAL_POPUP_NOT_FOUND(HttpStatus.BAD_REQUEST, "요청한 일부 팝업이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
