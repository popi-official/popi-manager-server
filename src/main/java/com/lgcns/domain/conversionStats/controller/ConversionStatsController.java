package com.lgcns.domain.conversionStats.controller;

import com.lgcns.domain.conversionStats.dto.response.ConversionStatsResponse;
import com.lgcns.domain.conversionStats.service.ConversionStatsService;
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
@Tag(name = "12. 구매전환율 분석 API", description = "구매전환율 분석 관련 API 입니다.")
public class ConversionStatsController {

    private final ConversionStatsService conversionStatsService;

    @GetMapping("/conversion-ratio")
    @Operation(summary = "구매전환율 조회", description = "특정 상품에 대한 관심자 수와 구매자 수의 비율을 계산하여 구매전환율을 조회합니다.")
    public ConversionStatsResponse conversionItemsFind(@PathVariable Long popupId) {
        return conversionStatsService.findTopAndLowConversionItems(popupId);
    }
}
