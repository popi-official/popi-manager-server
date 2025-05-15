package com.lgcns.domain.itemAnalysis.dto.response;

public record ItemPopularityResponse(Long itemId, int popularityScore) {
    public static ItemPopularityResponse of(Long itemId, int popularityScore) {
        return new ItemPopularityResponse(itemId, popularityScore);
    }
}
