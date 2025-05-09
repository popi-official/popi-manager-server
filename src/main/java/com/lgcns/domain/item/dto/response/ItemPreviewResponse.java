package com.lgcns.domain.item.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ItemPreviewResponse(
        @Schema(description = "상품 상세 위치", example = "1") String location,
        @Schema(description = "상품 ID", example = "1") Long itemId,
        @Schema(description = "상품명", example = "지수 포토카드") String name,
        @Schema(description = "상품 이미지 URL", example = "https://bucket/asdf") String imageUrl,
        @Schema(description = "상품 가격", example = "50000") int price,
        @Schema(description = "상품 재고 수량", example = "50") int stock) {
    public static ItemPreviewResponse of(
            String location, Long itemId, String name, String imageUrl, int price, int stock) {
        return new ItemPreviewResponse(location, itemId, name, imageUrl, price, stock);
    }
}
