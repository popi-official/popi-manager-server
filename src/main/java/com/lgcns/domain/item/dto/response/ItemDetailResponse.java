package com.lgcns.domain.item.dto.response;

import com.lgcns.domain.item.domain.Item;
import io.swagger.v3.oas.annotations.media.Schema;

public record ItemDetailResponse(
        @Schema(description = "상품 ID", example = "1") Long id,
        @Schema(description = "팝업 ID", example = "1") Long popupId,
        @Schema(description = "상품명", example = "지수 포토카드") String name,
        @Schema(description = "상품 이미지 URL", example = "https://bucket/asdf.jpg") String imageUrl,
        @Schema(description = "상품 가격", example = "50000") int price,
        @Schema(description = "상품 재고 수량", example = "50") int stock,
        @Schema(description = "상품 최소 발주 수량", example = "30") int minStock,
        @Schema(description = "상품 위치", example = "a1") String location) {
    public static ItemDetailResponse from(Item item) {
        return new ItemDetailResponse(
                item.getId(),
                item.getPopup().getId(),
                item.getName(),
                item.getImageUrl(),
                item.getPrice(),
                item.getStock(),
                item.getMinStock(),
                item.getLocation());
    }
}
