package com.lgcns.domain.itemAnalysis.dto;

public record ItemScore(Long itemId, int popularityScore, int salesVolume) {
    public static ItemScore of(Long itemId, int popularityScore, int salesVolume) {
        return new ItemScore(itemId, popularityScore, salesVolume);
    }
}
