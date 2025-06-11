package com.lgcns.domain.reservationStats.scheduler;

import com.lgcns.domain.reservationStats.service.ReservationStatsService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DayOfWeekReservationStatsScheduler {

    private final ReservationStatsService reservationStatsService;

    @Scheduled(cron = "0 0 0 * * *")
    public void updateDayOfWeekReservationStats() {
        try {
            updateStats();
        } catch (Exception e) {
            log.error("요일별 예약 통계 업데이트 스케줄러 실패: {}", e.getMessage());
            scheduleRetry();
        }
    }

    private void updateStats() {
        reservationStatsService.updateAllDayOfWeekReservationStats();
    }

    private void scheduleRetry() {
        // 10분 후 첫 번째 재시도
        CompletableFuture.delayedExecutor(10, TimeUnit.MINUTES).execute(() -> executeRetry(1));
    }

    private void executeRetry(int attemptNumber) {
        try {
            updateStats();
            log.info("요일별 예약 통계 업데이트 재시도 {} 성공", attemptNumber);
        } catch (Exception e) {
            log.error("요일별 예약 통계 업데이트 재시도 {} 실패: {}", attemptNumber, e.getMessage());

            if (attemptNumber < 2) {
                // 10분 후 마지막
                CompletableFuture.delayedExecutor(10, TimeUnit.MINUTES)
                        .execute(() -> executeRetry(attemptNumber + 1));
            } else {
                log.error("요일별 예약 통계 업데이트 모든 재시도 실패");
            }
        }
    }
}
