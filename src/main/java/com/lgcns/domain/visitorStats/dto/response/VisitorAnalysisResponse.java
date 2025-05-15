package com.lgcns.domain.visitorStats.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record VisitorAnalysisResponse(
        @Schema(description = "성별 그룹 통계", implementation = CountAndRatioResponse.class)
                List<CountAndRatioResponse> gender,
        @Schema(description = "나이별 그룹 통계", implementation = CountAndRatioResponse.class)
                List<CountAndRatioResponse> age) {
    public static VisitorAnalysisResponse of(
            List<CountAndRatioResponse> gender, List<CountAndRatioResponse> age) {
        return new VisitorAnalysisResponse(gender, age);
    }
}
