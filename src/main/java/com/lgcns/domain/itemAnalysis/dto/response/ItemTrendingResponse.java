package com.lgcns.domain.itemAnalysis.dto.response;

import com.lgcns.domain.item.domain.Item;

public record ItemTrendingResponse(
        Long itemId, String title, String imagePath, Integer price, Integer stock) {
    public static ItemTrendingResponse from(Item item) {
        return new ItemTrendingResponse(
                item.getId(), item.getName(), item.getImageUrl(), item.getPrice(), item.getStock());
    }
}
