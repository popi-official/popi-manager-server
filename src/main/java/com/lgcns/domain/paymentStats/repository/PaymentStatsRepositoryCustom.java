package com.lgcns.domain.paymentStats.repository;

import com.lgcns.domain.paymentStats.dto.response.PaymentAverageResponse;
import java.time.LocalDate;
import java.time.LocalTime;

public interface PaymentStatsRepositoryCustom {
    PaymentAverageResponse findAveragePayment(
            Long popupId, LocalDate openDate, LocalDate today, LocalTime currentTime);
}
