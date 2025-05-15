package com.lgcns.domain.paymentStats.repository;

import com.lgcns.domain.paymentStats.dto.response.PaymentAverageResponse;
import java.time.LocalDate;

public interface PaymentStatsRepositoryCustom {
    PaymentAverageResponse getPaymentAverages(Long popupId, LocalDate today);
}
