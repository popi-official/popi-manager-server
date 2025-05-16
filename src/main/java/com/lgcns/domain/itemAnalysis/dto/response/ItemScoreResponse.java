package com.lgcns.domain.itemAnalysis.dto.response;

public record ItemScoreResponse(Long itemId, int totalScore, int popularityScore, int salesVolume) {
    public static ItemScoreResponse of(Long itemId, int popularityScore, int salesVolume) {
        return new ItemScoreResponse(
                itemId, popularityScore + salesVolume, popularityScore, salesVolume);
    }
}
