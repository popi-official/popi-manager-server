package com.lgcns.infra.scheduler;

import com.lgcns.domain.visitorStats.domain.VisitorStats;
import com.lgcns.domain.visitorStats.service.VisitorStatsService;
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
public class VisitorStatsStepConfig {

    private final VisitorStatsService visitorStatsService;

    @Bean
    @StepScope
    public ItemReader<Long> createVisitorStatsItemReader() {
        return new ListItemReader<>(visitorStatsService.findTargetPopupIds());
    }

    @Bean
    @StepScope
    public ItemProcessor<Long, VisitorStats> createVisitorStatsItemProcessor() {
        return visitorStatsService::convertVisitorStats;
    }

    @Bean
    @StepScope
    public ItemWriter<VisitorStats> createVisitorStatsItemWriter() {
        return chunk -> {
            List<VisitorStats> visitorStatsList = new ArrayList<>(chunk.getItems());
            visitorStatsService.createVisitorStats(visitorStatsList);
        };
    }
}
