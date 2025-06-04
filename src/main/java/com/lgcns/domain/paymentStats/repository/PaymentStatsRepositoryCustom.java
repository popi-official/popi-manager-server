package com.lgcns.domain.paymentStats.repository;

import com.lgcns.domain.paymentStats.dto.response.AverageAmountResponse;

public interface PaymentStatsRepositoryCustom {
    AverageAmountResponse findLatestAverageAmountByPopupId(Long popupId);
}
