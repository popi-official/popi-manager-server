package com.lgcns.domain.item.exception;

import com.lgcns.global.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ItemErrorCode implements ErrorCode {
    EMPTY_ITEM_LIST(HttpStatus.NOT_FOUND, "팝업에 등록된 상품이 없습니다."),
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다."),
    ITEM_DELETE_UNAUTHORIZED(HttpStatus.FORBIDDEN, "상품 삭제 권한이 없습니다.");
    private final HttpStatus httpStatus;
    private final String message;
}
