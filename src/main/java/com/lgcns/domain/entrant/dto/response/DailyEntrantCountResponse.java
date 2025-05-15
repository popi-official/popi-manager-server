package com.lgcns.domain.entrant.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record DailyEntrantCountResponse(
        @Schema(description = "입장자 수", example = "100") Integer entrantCount) {
    public static DailyEntrantCountResponse of(Integer entrantCount) {
        return new DailyEntrantCountResponse(entrantCount);
    }
}
