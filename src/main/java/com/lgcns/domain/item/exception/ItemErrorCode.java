package com.lgcns.domain.item.exception;

import com.lgcns.global.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ItemErrorCode implements ErrorCode {
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다."),
    ITEM_POPUP_MISMATCH(HttpStatus.BAD_REQUEST, "해당 팝업에 등록된 상품이 아닙니다.");
    private final HttpStatus httpStatus;
    private final String message;
}
