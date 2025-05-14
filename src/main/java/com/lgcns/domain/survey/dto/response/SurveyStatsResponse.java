package com.lgcns.domain.survey.dto.response;

import java.util.List;

public record SurveyStatsResponse(int surveyNumber, List<ChoiceStatsResponse> contents) {
    public static SurveyStatsResponse of(int surveyNumber, List<ChoiceStatsResponse> contents) {
        return new SurveyStatsResponse(surveyNumber, contents);
    }
}
