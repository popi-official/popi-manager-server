package com.lgcns.domain.item.dto;

import com.lgcns.domain.item.domain.Item;

public record ItemScore(Item item, int popularityScore) {
    public static ItemScore of(Item item, int popularityScore) {
        return new ItemScore(item, popularityScore);
    }
}
