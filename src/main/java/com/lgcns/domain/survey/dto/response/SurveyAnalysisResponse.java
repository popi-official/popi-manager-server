package com.lgcns.domain.survey.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record SurveyAnalysisResponse(
        @Schema(description = "전체 응답자수", example = "3032") Long totalCount,
        @Schema(description = "설문 문항별 통계", implementation = SurveyStatsResponse.class)
                List<SurveyStatsResponse> surveys) {
    public static SurveyAnalysisResponse of(Long totalCount, List<SurveyStatsResponse> surveys) {
        return new SurveyAnalysisResponse(totalCount, surveys);
    }
}
