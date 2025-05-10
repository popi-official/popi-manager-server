package com.lgcns.domain.item.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemMinStockUpdateRequest(
        @Schema(description = "상품 최소 발주 수량", example = "30")
                @NotNull(message = "최소 발주 수량은 필수 입력 항목입니다.")
                @Min(value = 0, message = "최소 발주 수량은 0 이상이어야 합니다.")
                Integer minStock) {}
