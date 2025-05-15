package com.lgcns.domain.visitorStats.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record VisitorStatsResponse(
        @Schema(description = "남성 수", example = "25") Integer maleCount,
        @Schema(description = "여성 수", example = "25") Integer femaleCount,
        @Schema(description = "10대 수", example = "15") Integer teenCount,
        @Schema(description = "20대 수", example = "15") Integer twentyCount,
        @Schema(description = "30대 수", example = "10") Integer thirtyCount,
        @Schema(description = "40대 수", example = "10") Integer fortyCount) {}
