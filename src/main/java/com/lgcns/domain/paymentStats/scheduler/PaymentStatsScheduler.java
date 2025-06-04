package com.lgcns.domain.paymentStats.scheduler;

import com.lgcns.domain.paymentStats.service.PaymentStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStatsScheduler {

    private final PaymentStatsService paymentStatsService;

    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void createPaymentStats() {
        paymentStatsService.createPaymentStats();
    }
}
