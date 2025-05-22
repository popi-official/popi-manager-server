package com.lgcns.domain.visitorStats.externalApi;

import com.lgcns.domain.visitorStats.dto.response.VisitorAnalysisResponse;
import com.lgcns.domain.visitorStats.service.VisitorStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/popups/{popupId}/dashboard")
@RequiredArgsConstructor
@Tag(name = "7. 방문자 분석 API", description = "대시보드에서 방문자 분석을 위한 API입니다.")
public class VisitorStatsController {

    private final VisitorStatsService visitorStatsService;

    @GetMapping("/visitors")
    @Operation(summary = "방문자 분석 조회", description = "방문자의 성별과 연령대의 수와 비율을 조회합니다.")
    public VisitorAnalysisResponse visitorAnalysisGet(@PathVariable Long popupId) {
        return visitorStatsService.getVisitorAnalysis(popupId);
    }
}
