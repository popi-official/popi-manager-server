package com.lgcns.domain.item.kafka.message;

import java.time.LocalDateTime;
import java.util.List;

public record ItemPurchasedMessage(
        Long popupId, List<Item> items, int amount, LocalDateTime purchasedAt) {
    public record Item(Long itemId, Integer quantity) {}
}
