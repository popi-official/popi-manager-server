package com.lgcns.infra.scheduler;

import com.lgcns.domain.visitorStats.service.VisitorStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VisitorStatsScheduler {

    private final VisitorStatsService visitorStatsService;

    @Scheduled(cron = "0 0 * * * *")
    public void createVisitorStats() {
        visitorStatsService.createVisitorStats();
    }
}
