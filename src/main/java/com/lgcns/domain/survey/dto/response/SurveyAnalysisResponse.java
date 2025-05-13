package com.lgcns.domain.survey.dto.response;

import java.util.List;

public record SurveyAnalysisResponse(int totalCount, List<SurveyResultResponse> surveys) {
    public static SurveyAnalysisResponse of(int totalCount, List<SurveyResultResponse> surveys) {
        return new SurveyAnalysisResponse(totalCount, surveys);
    }
}
