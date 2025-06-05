package com.lgcns.domain.conversionStats.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ConversionItem(
        @Schema(description = "상품명", example = "POSTER SET") String name,
        @Schema(description = "관심자 수", example = "200") Integer interested,
        @Schema(description = "구매자 수", example = "100") Integer purchased,
        @Schema(description = "구매 전환율(%)", example = "50") Integer conversionRatio) {}
