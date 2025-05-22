package com.lgcns.domain.itemAnalysis.externalApi;

import com.lgcns.domain.itemAnalysis.dto.response.ItemTrendingResponse;
import com.lgcns.domain.itemAnalysis.service.ItemAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/popups/{popupId}/dashboard")
@RequiredArgsConstructor
@Tag(name = "13. 인기 상품 분석 API", description = "인기 상품 분석 API 입니다.")
public class ItemAnalysisController {

    private final ItemAnalysisService itemAnalysisService;

    @GetMapping("/trending")
    @Operation(
            summary = "실시간 인기 상품 조회",
            description = "팝업 오픈부터 직전 타임까지의 관심도와 판매량을 기준으로 상위 3개의 인기 상품을 조회합니다.")
    public List<ItemTrendingResponse> TrendingItemFind(@PathVariable Long popupId) {
        return itemAnalysisService.getTrendingItems(popupId);
    }
}
