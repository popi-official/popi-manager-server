package com.lgcns.domain.paymentStats.service;

import com.lgcns.domain.paymentStats.dto.response.PaymentAverageResponse;

public interface PaymentStatsService {
    PaymentAverageResponse getPaymentAverage(Long popupId);
}
