package com.lgcns.domain.item.scheduler;

import com.lgcns.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemAverageSalesScheduler {

    private final ItemService itemService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void runAverageSalesUpdate() {
        itemService.calculateItemAverageSales();
    }
}
