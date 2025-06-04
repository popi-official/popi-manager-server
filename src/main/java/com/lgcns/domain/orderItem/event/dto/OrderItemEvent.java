package com.lgcns.domain.orderItem.event.dto;

public record OrderItemEvent(Long itemId) {
    public static OrderItemEvent of(Long itemId) {
        return new OrderItemEvent(itemId);
    }
}
