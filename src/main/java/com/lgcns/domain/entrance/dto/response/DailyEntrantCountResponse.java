package com.lgcns.domain.entrance.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record DailyEntrantCountResponse(
        @Schema(description = "입장자 수", example = "100") Long entrantCount) {
    public static DailyEntrantCountResponse of(Long entrantCount) {
        return new DailyEntrantCountResponse(entrantCount);
    }
}
