package com.lgcns.domain.conversionStats.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ConversionItemsResponse(
        @Schema(description = "구매 전환율 하위 상품 TOP 6") List<ConversionItem> low,
        @Schema(description = "구매 전환율 상위 상품 TOP 6") List<ConversionItem> high) {
    public static ConversionItemsResponse of(List<ConversionItem> low, List<ConversionItem> high) {
        return new ConversionItemsResponse(low, high);
    }
}
