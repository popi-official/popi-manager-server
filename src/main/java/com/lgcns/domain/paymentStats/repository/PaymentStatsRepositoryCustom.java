package com.lgcns.domain.paymentStats.repository;

import com.lgcns.domain.paymentStats.domain.PaymentStats;
import com.lgcns.domain.paymentStats.dto.response.AverageAmountResponse;
import java.util.List;

public interface PaymentStatsRepositoryCustom {
    AverageAmountResponse findLatestAverageAmountByPopupId(Long popupId);

    void bulkInsertPaymentStats(List<PaymentStats> statsList);
}
