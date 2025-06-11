package com.lgcns.domain.congestionStats.externalApi;

import com.lgcns.domain.congestionStats.dto.response.CongestionStatsResponse;
import com.lgcns.domain.congestionStats.service.CongestionStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/popups/{popupId}/dashboard/congestion")
@Tag(name = "09. 혼잡도 분석 API", description = "혼잡도 분석 관련 API 입니다.")
public class CongestionStatsController {

    private final CongestionStatsService congestionStatsService;

    @GetMapping
    @Operation(summary = "혼잡도 분석 조회", description = "요일별로 2시간 단위 혼잡도 분석을 조회합니다.")
    public CongestionStatsResponse congestionStatsGet(@PathVariable("popupId") Long popupId) {
        return congestionStatsService.getCongestionStats(popupId);
    }
}
