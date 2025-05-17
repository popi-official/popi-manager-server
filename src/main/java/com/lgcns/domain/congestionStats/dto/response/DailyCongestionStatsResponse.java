package com.lgcns.domain.congestionStats.dto.response;

import com.lgcns.domain.reservationStats.dto.response.DayOfWeek;
import io.swagger.v3.oas.annotations.media.Schema;

public record DailyCongestionStatsResponse(
        @Schema(description = "요일", example = "월") DayOfWeek dayOfWeek,
        @Schema(description = "혼잡도 분석 시각", example = "6") Integer time,
        @Schema(description = "혼잡도", example = "10") Integer value) {

    public static DailyCongestionStatsResponse of(
            DayOfWeek dayOfWeek, Integer time, Integer value) {
        return new DailyCongestionStatsResponse(dayOfWeek, time, value);
    }
}
