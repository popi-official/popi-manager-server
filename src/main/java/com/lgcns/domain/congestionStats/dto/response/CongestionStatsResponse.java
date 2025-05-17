package com.lgcns.domain.congestionStats.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record CongestionStatsResponse(
        @Schema(description = "월요일 혼잡도 분석", implementation = DailyCongestionStatsResponse.class)
                List<DailyCongestionStatsResponse> mon,
        @Schema(description = "화요일 혼잡도 분석", implementation = DailyCongestionStatsResponse.class)
                List<DailyCongestionStatsResponse> tue,
        @Schema(description = "수요일 혼잡도 분석", implementation = DailyCongestionStatsResponse.class)
                List<DailyCongestionStatsResponse> wed,
        @Schema(description = "목요일 혼잡도 분석", implementation = DailyCongestionStatsResponse.class)
                List<DailyCongestionStatsResponse> thu,
        @Schema(description = "금요일 혼잡도 분석", implementation = DailyCongestionStatsResponse.class)
                List<DailyCongestionStatsResponse> fri,
        @Schema(description = "토요일 혼잡도 분석", implementation = DailyCongestionStatsResponse.class)
                List<DailyCongestionStatsResponse> sat,
        @Schema(description = "일요일 혼잡도 분석", implementation = DailyCongestionStatsResponse.class)
                List<DailyCongestionStatsResponse> sun) {}
