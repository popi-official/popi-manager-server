package com.lgcns.domain.conversionStats.scheduler;

import com.lgcns.domain.conversionStats.service.ConversionStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConversionStatsScheduler {

    private final ConversionStatsService conversionStatsService;

    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void createConversionStats() {
        conversionStatsService.createConversionStats();
    }
}
