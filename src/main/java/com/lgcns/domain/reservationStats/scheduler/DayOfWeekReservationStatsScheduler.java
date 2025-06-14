package com.lgcns.domain.reservationStats.scheduler;

import com.lgcns.domain.reservationStats.service.ReservationStatsService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DayOfWeekReservationStatsScheduler {

    private final ReservationStatsService reservationStatsService;

    @Scheduled(cron = "0 0 0 * * *")
    public void updateDayOfWeekReservationStats() {
        try {
            updateStats();
        } catch (Exception e) {
            scheduleRetry();
        }
    }

    private void updateStats() {
        reservationStatsService.updateAllDayOfWeekReservationStats();
    }

    private void scheduleRetry() {
        CompletableFuture.delayedExecutor(10, TimeUnit.MINUTES).execute(() -> executeRetry(1));
    }

    private void executeRetry(int attemptNumber) {
        try {
            updateStats();
        } catch (Exception e) {
            if (attemptNumber < 2) {
                CompletableFuture.delayedExecutor(10, TimeUnit.MINUTES)
                        .execute(() -> executeRetry(attemptNumber + 1));
            }
        }
    }
}
