package com.lgcns.domain.survey.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChoiceStatsResponse(
        @Schema(description = "선지 이름", example = "포토카드") String title,
        @Schema(description = "응답자수", example = "1000") Long selectedCount,
        @Schema(description = "비율", example = "42") Integer ratio) {
    public static ChoiceStatsResponse of(String title, Long selectedCount, Integer ratio) {
        return new ChoiceStatsResponse(title, selectedCount, ratio);
    }
}
