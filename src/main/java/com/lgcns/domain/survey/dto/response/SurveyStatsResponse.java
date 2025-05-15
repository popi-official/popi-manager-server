package com.lgcns.domain.survey.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record SurveyStatsResponse(
        @Schema(description = "설문 문항 번호", example = "1") Integer surveyNumber,
        @Schema(description = "선지별 통계", implementation = ChoiceStatsResponse.class)
                List<ChoiceStatsResponse> contents) {
    public static SurveyStatsResponse of(Integer surveyNumber, List<ChoiceStatsResponse> contents) {
        return new SurveyStatsResponse(surveyNumber, contents);
    }
}
