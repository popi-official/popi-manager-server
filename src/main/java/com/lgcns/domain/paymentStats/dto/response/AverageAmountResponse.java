package com.lgcns.domain.paymentStats.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AverageAmountResponse(
        @Schema(description = "전체 기간 1인 평균 구매액", example = "30000") Integer totalAverageAmount,
        @Schema(description = "오늘 1인 평균 구매액", example = "5000") Integer todayAverageAmount) {}
