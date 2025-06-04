package com.lgcns.domain.visitorStats.batch;

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
public class VisitorStatsStepManager {

    private final VisitorStatsService visitorStatsService;
    private static final String CREATE_VISITOR_STATS_ITEM_READER = "createVisitorStatsItemReader";
    private static final String CREATE_VISITOR_STATS_ITEM_PROCESSOR =
            "createVisitorStatsItemProcessor";
    private static final String CREATE_VISITOR_STATS_ITEM_WRITER = "createVisitorStatsItemWriter";

    @Bean(name = CREATE_VISITOR_STATS_ITEM_READER)
    @StepScope
    public ItemReader<Long> createVisitorStatsItemReader() {
        ListItemReader<Long> delegate =
                new ListItemReader<>(visitorStatsService.findTargetPopupIds());

        return new ItemReader<>() {
            @Override
            public synchronized Long read() {
                return delegate.read();
            }
        };
    }

    @Bean(name = CREATE_VISITOR_STATS_ITEM_PROCESSOR)
    @StepScope
    public ItemProcessor<Long, VisitorStats> createVisitorStatsItemProcessor() {
        return visitorStatsService::convertVisitorStats;
    }

    @Bean(name = CREATE_VISITOR_STATS_ITEM_WRITER)
    @StepScope
    public ItemWriter<VisitorStats> createVisitorStatsItemWriter() {
        return chunk -> {
            List<VisitorStats> visitorStatsList = new ArrayList<>(chunk.getItems());
            visitorStatsService.createVisitorStats(visitorStatsList);
        };
    }
}
