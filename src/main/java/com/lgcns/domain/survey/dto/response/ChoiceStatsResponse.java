package com.lgcns.domain.survey.dto.response;

public record ChoiceStatsResponse(String choiceContent, int selectedCount, double ratio) {
    public static ChoiceStatsResponse of(String choiceContent, int selectedCount, double ratio) {
        return new ChoiceStatsResponse(choiceContent, selectedCount, ratio);
    }
}
