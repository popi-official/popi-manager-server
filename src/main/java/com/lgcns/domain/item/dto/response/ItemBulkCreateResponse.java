package com.lgcns.domain.item.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 일괄 등록 응답 DTO")
public record ItemBulkCreateResponse(
        @Schema(description = "처리된 총 행 수", example = "20") int totalRows,
        @Schema(description = "성공한 상품 수", example = "20") int successCount,
        @Schema(description = "처리 결과 메시지", example = "20개 상품이 성공적으로 등록되었습니다.") String message) {
    public static ItemBulkCreateResponse of(int totalRows, int successCount, String message) {
        return new ItemBulkCreateResponse(totalRows, successCount, message);
    }

    public static ItemBulkCreateResponse success(int successCount) {
        return new ItemBulkCreateResponse(
                successCount, successCount, String.format("%d개 상품이 성공적으로 등록되었습니다.", successCount));
    }
}
