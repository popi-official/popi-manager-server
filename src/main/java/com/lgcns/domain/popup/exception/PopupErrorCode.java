package com.lgcns.domain.popup.error;

import com.lgcns.global.error.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PopupErrorCode implements ErrorCode {
    POPUP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 팝업이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
