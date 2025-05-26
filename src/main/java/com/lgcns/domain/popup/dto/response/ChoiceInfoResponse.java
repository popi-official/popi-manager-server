package com.lgcns.domain.popup.dto.response;

public record ChoiceInfoResponse(Long surveyId, Long choiceId, String content) {
    public static ChoiceInfoResponse of(Long surveyId, Long choiceId, String content) {
        return new ChoiceInfoResponse(surveyId, choiceId, content);
    }
}
