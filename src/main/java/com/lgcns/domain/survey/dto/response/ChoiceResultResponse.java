package com.lgcns.domain.survey.dto.response;

public record ChoiceResultResponse(String choiceContent, int selectedCount, double ratio) {
    public static ChoiceResultResponse of(String choiceContent, int selectedCount, double ratio) {
        return new ChoiceResultResponse(choiceContent, selectedCount, ratio);
    }
}
