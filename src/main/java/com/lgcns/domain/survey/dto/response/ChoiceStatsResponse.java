package com.lgcns.domain.survey.dto.response;

public record ChoiceStatsResponse(String title, Long selectedCount, Double ratio) {
    public static ChoiceStatsResponse of(String title, Long selectedCount, Double ratio) {
        return new ChoiceStatsResponse(title, selectedCount, ratio);
    }
}
