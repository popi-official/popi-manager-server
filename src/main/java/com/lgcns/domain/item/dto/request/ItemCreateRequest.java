package com.lgcns.domain.item.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ItemCreateRequest(
        @NotNull(message = "상품 이름은 필수입니다.") @Schema(description = "상품 이름", example = "팝업 포스터")
                String name,
        @NotNull(message = "상품 가격은 필수입니다.") @Schema(description = "상품 가격", example = "10000")
                String price,
        @NotNull(message = "상품 사진은 필수입니다.")
                @Schema(description = "상품 사진", example = "aws.bucket.상품사진")
                String imageUrl,
        @NotNull(message = "상품 수량은 필수입니다.") @Schema(description = "상품 수량", example = "100") int qty,
        @NotNull(message = "최소 발주 수량은 필수입니다.") @Schema(description = "최소 발주 수량", example = "10")
                int minQty,
        @NotNull(message = "상품 위치는 필수입니다.") @Schema(description = "상품 위치", example = "A1")
                String location) {
    public static ItemCreateRequest of(
            String name, String price, String imageUrl, int qty, int minQty, String location) {
        return new ItemCreateRequest(name, price, imageUrl, qty, minQty, location);
    }
}
