package com.lgcns.domain.item.exception;

import com.lgcns.global.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ItemErrorCode implements ErrorCode {
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다."),
    ITEM_POPUP_MISMATCH(HttpStatus.BAD_REQUEST, "해당 팝업에 등록된 상품이 아닙니다."),
    MIN_STOCK_EXCEEDED(HttpStatus.BAD_REQUEST, "최소 발주 수량은 재고 수량보다 크게 설정할 수 없습니다."),

    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "요청한 수량이 현재 재고보다 많습니다.");
    private final HttpStatus httpStatus;
    private final String message;
}
