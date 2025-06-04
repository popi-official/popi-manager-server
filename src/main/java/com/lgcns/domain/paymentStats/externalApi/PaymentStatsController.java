package com.lgcns.domain.paymentStats.externalApi;

import com.lgcns.domain.paymentStats.dto.response.AverageAmountResponse;
import com.lgcns.domain.paymentStats.service.PaymentStatsService;
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
@Tag(name = "5. 1인 평균 구매액 분석 API", description = "1인 평균 구매액 분석 관련 API 입니다.")
public class PaymentStatsController {

    private final PaymentStatsService paymentStatsService;

    @GetMapping("/average-purchase")
    @Operation(summary = "1인당 평균 구매액 조회", description = "팝업의 총 평균 구매액과 오늘의 평균 구매액을 조회합니다.")
    public AverageAmountResponse averageAmountFind(@PathVariable Long popupId) {
        return paymentStatsService.findLatestAverageAmount(popupId);
    }
}
