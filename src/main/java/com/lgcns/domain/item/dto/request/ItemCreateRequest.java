package com.lgcns.domain.item.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemCreateRequest(
        @NotNull(message = "팝업 아이디는 필수입니다.") @Schema(description = "팝업 아이디", example = "1")
                Long popupId,
        @NotBlank(message = "상품 이름은 필수입니다.") @Schema(description = "상품 이름", example = "팝업 포스터")
                String name,
        @NotBlank(message = "상품 사진은 필수입니다.")
                @Schema(description = "상품 사진", example = "aws.bucket.상품사진")
                String imageUrl,
        @NotNull(message = "상품 가격은 필수입니다.") @Schema(description = "상품 가격", example = "10000")
                int price,
        @NotNull(message = "상품 수량은 필수입니다.")
                @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
                @Schema(description = "상품 수량", example = "100")
                int stock,
        @NotNull(message = "최소 발주 수량은 필수입니다.")
                @Min(value = 0, message = "발주 수량은 0 이상이어야 합니다.")
                @Schema(description = "최소 발주 수량", example = "10")
                int minStock,
        @NotBlank(message = "상품 위치는 필수입니다.") @Schema(description = "상품 위치", example = "A1")
                String location) {
    public static ItemCreateRequest of(
            Long popupId,
            String name,
            String imageUrl,
            int price,
            int stock,
            int minStock,
            String location) {
        return new ItemCreateRequest(popupId, name, imageUrl, price, stock, minStock, location);
    }
}
