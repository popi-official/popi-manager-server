package com.lgcns.domain.paymentStats.repository;

import com.lgcns.domain.paymentStats.dto.response.AverageAmountResponse;
import java.time.LocalDate;

public interface PaymentStatsRepositoryCustom {
    AverageAmountResponse getPaymentAverages(Long popupId, LocalDate today);
}
