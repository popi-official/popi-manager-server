package com.lgcns.domain.survey.dto.response;

import java.util.List;

public record SurveyResultResponse(int surveyNumber, List<ChoiceResultResponse> contents) {
    public static SurveyResultResponse of(int surveyNumber, List<ChoiceResultResponse> contents) {
        return new SurveyResultResponse(surveyNumber, contents);
    }
}
