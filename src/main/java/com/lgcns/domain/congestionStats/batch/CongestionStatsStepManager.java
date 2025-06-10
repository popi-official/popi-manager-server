package com.lgcns.domain.congestionStats.batch;

import com.lgcns.domain.congestionStats.domain.CongestionStats;
import com.lgcns.domain.congestionStats.service.CongestionStatsService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CongestionStatsStepManager {

    private final CongestionStatsService congestionStatsService;

    public static final String CREATE_CONGESTION_STATS_ITEM_READER =
            "createCongestionStatsItemReader";
    public static final String CREATE_CONGESTION_STATS_ITEM_PROCESSOR =
            "createCongestionStatsItemProcessor";
    public static final String CREATE_CONGESTION_STATS_ITEM_WRITER =
            "createCongestionStatsItemWriter";

    @Bean(name = CREATE_CONGESTION_STATS_ITEM_READER)
    @StepScope
    public ItemReader<Long> createCongestionStatsItemReader() {
        ListItemReader<Long> delegate =
                new ListItemReader<>(congestionStatsService.findTargetPopupIds());

        return new ItemReader<>() {
            @Override
            public synchronized Long read() {
                return delegate.read();
            }
        };
    }

    @Bean(name = CREATE_CONGESTION_STATS_ITEM_PROCESSOR)
    @StepScope
    public ItemProcessor<Long, CongestionStats> createCongestionStatsItemProcessor() {
        return congestionStatsService::convertCongestionStats;
    }

    @Bean(name = CREATE_CONGESTION_STATS_ITEM_WRITER)
    @StepScope
    public ItemWriter<CongestionStats> createCongestionStatsItemWriter() {
        return chunk -> {
            List<CongestionStats> congestionStatsList = new ArrayList<>(chunk.getItems());
            congestionStatsService.createCongestionStats(congestionStatsList);
        };
    }
}
