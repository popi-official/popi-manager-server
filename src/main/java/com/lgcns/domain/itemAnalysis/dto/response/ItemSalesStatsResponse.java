package com.lgcns.domain.itemAnalysis.dto.response;

import com.lgcns.domain.itemAnalysis.domain.ItemSalesStats;

public record ItemSalesStatsResponse(Long itemId, int salesVolume) {
    public static ItemSalesStatsResponse from(ItemSalesStats entity) {
        return new ItemSalesStatsResponse(entity.getItemId(), entity.getSalesVolume());
    }
}
