package com.lgcns.domain.item.dto.response;

public record ItemLocationResponse(
        Long id,
        String name,
        String imageUrl,
        int price,
        int stock,
        int minStock,
        String locationGroup, // 위치의 첫 글자 (a, b, c 등)
        String locationNumber // 위치의 숫자 부분
        ) {
    public ItemPreviewResponse toPreviewResponse() {
        return ItemPreviewResponse.of(locationNumber, id, name, imageUrl, price, stock, minStock);
    }
}
