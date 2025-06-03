package com.lgcns.domain.paymentStats.service;

import com.lgcns.domain.paymentStats.dto.response.AverageAmountResponse;

public interface PaymentStatsService {
    AverageAmountResponse getPaymentAverages(Long popupId);
}
