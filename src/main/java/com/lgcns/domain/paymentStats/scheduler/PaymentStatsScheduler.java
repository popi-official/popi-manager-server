package com.lgcns.domain.paymentStats.scheduler;

import com.lgcns.domain.paymentStats.client.PaymentServiceClient;
import com.lgcns.domain.paymentStats.domain.AveragePeriod;
import com.lgcns.domain.paymentStats.domain.PaymentStats;
import com.lgcns.domain.paymentStats.dto.response.AverageAmountResponse;
import com.lgcns.domain.paymentStats.repository.PaymentStatsRepository;
import com.lgcns.domain.popup.repository.PopupRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStatsScheduler {

    private final PopupRepository popupRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PaymentStatsRepository paymentStatsRepository;

    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void createPaymentStats() {
        List<Long> popupIds = popupRepository.findAllPopupIds();

        List<PaymentStats> batch = new ArrayList<>();

        for (Long popupId : popupIds) {
            try {
                AverageAmountResponse response = paymentServiceClient.findAverageAmount(popupId);

                PaymentStats totalStats =
                        PaymentStats.createPaymentStats(
                                popupId,
                                response.totalAverageAmount(),
                                AveragePeriod.TOTAL,
                                LocalDate.now(),
                                LocalTime.now());

                PaymentStats todayStats =
                        PaymentStats.createPaymentStats(
                                popupId,
                                response.todayAverageAmount(),
                                AveragePeriod.TODAY,
                                LocalDate.now(),
                                LocalTime.now());

                batch.add(totalStats);
                batch.add(todayStats);

            } catch (Exception e) {
                log.warn("Failed to prepare stats for popupId: {}", popupId, e);
            }
        }

        if (!batch.isEmpty()) {
            try {
                paymentStatsRepository.bulkInsertPaymentStats(batch);
            } catch (Exception e) {
                log.error("Failed to insert payment stats batch", e);
            }
        }
    }
}
