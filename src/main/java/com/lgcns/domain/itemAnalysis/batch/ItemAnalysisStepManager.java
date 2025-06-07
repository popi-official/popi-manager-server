package com.lgcns.domain.itemAnalysis.batch;

import com.lgcns.domain.itemAnalysis.domain.ItemAnalysis;
import com.lgcns.domain.itemAnalysis.service.ItemAnalysisService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ItemAnalysisStepManager {

    private final ItemAnalysisService itemAnalysisService;

    public static final String UPDATE_ITEM_ANALYSIS_ITEM_READER = "updateItemAnalysisItemReader";
    public static final String UPDATE_ITEM_ANALYSIS_ITEM_PROCESSOR =
            "updateItemAnalysisItemProcessor";
    public static final String UPDATE_ITEM_ANALYSIS_ITEM_WRITER = "updateItemAnalysisItemWriter";

    @Bean(name = UPDATE_ITEM_ANALYSIS_ITEM_READER)
    @StepScope
    public ItemReader<Long> updateItemAnalysisItemReader() {
        ListItemReader<Long> delegate =
                new ListItemReader<>(itemAnalysisService.findTargetPopupIds());

        return new ItemReader<Long>() {
            @Override
            public synchronized Long read() {
                return delegate.read();
            }
        };
    }

    @Bean(name = UPDATE_ITEM_ANALYSIS_ITEM_PROCESSOR)
    @StepScope
    public ItemProcessor<Long, List<ItemAnalysis>> updateItemAnalysisItemProcessor() {
        return itemAnalysisService::processPopupItemAnalysis;
    }

    @Bean(name = UPDATE_ITEM_ANALYSIS_ITEM_WRITER)
    @StepScope
    public ItemWriter<List<ItemAnalysis>> updateItemAnalysisItemWriter() {
        return chunk -> {
            List<ItemAnalysis> allItemAnalyses = new ArrayList<>();

            for (List<ItemAnalysis> itemAnalysisList : chunk.getItems()) {
                if (itemAnalysisList != null) {
                    allItemAnalyses.addAll(itemAnalysisList);
                }
            }

            itemAnalysisService.saveItemAnalysisList(allItemAnalyses);
        };
    }
}
