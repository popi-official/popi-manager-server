package com.lgcns.domain.survey.dto.response;

public record ChoiceStatsResponse(String choiceContent, Long selectedCount, Double ratio) {
    public static ChoiceStatsResponse of(String choiceContent, Long selectedCount, Double ratio) {
        return new ChoiceStatsResponse(choiceContent, selectedCount, ratio);
    }
}
