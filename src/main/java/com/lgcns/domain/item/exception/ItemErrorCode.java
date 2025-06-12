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

    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "요청한 수량이 현재 재고보다 많습니다."),
    INVALID_RESTOCK(HttpStatus.BAD_REQUEST, "재고 수량은 0 이상이어야 합니다."),

    EXCEL_FILE_INVALID(HttpStatus.BAD_REQUEST, "엑셀 파일이 올바르지 않습니다."),
    EXCEL_DATA_INVALID(HttpStatus.BAD_REQUEST, "엑셀 데이터가 올바르지 않습니다."),
    EXCEL_PROCESSING_FAILED(HttpStatus.BAD_REQUEST, "엑셀 파일 처리에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
