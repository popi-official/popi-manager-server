package com.lgcns.domain.item.dto.response;

import com.lgcns.domain.item.domain.Item;
import io.swagger.v3.oas.annotations.media.Schema;

public record ItemTrendingResponse(
        @Schema(description = "상품 ID", example = "1") Long itemId,
        @Schema(description = "상품명", example = "POSTER SET") String title,
        @Schema(description = "상품 이미지 URL", example = "https://bucket/asdf") String imagePath,
        @Schema(description = "상품 가격", example = "50000") Integer price,
        @Schema(description = "상품 재고", example = "100") Integer stock) {
    public static ItemTrendingResponse from(Item item) {
        return new ItemTrendingResponse(
                item.getId(), item.getName(), item.getImageUrl(), item.getPrice(), item.getStock());
    }
}
