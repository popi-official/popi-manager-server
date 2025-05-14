package com.lgcns.domain.survey.dto.response;

import java.util.List;

public record SurveyAnalysisResponse(Long totalCount, List<SurveyStatsResponse> surveys) {
    public static SurveyAnalysisResponse of(Long totalCount, List<SurveyStatsResponse> surveys) {
        return new SurveyAnalysisResponse(totalCount, surveys);
    }
}
