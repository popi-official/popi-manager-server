package com.lgcns.domain.item.batch;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.service.ItemService;
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
public class ItemStepManager {

    private final ItemService itemService;

    public static final String UPDATE_ITEM_ANALYSIS_READER = "updateItemAnalysisReader";
    public static final String UPDATE_ITEM_ANALYSIS_PROCESSOR = "updateItemAnalysisProcessor";
    public static final String UPDATE_ITEM_ANALYSIS_WRITER = "updateItemAnalysisWriter";

    @Bean(name = UPDATE_ITEM_ANALYSIS_READER)
    @StepScope
    public ItemReader<Long> updateItemAnalysisReader() {
        ListItemReader<Long> delegate = new ListItemReader<>(itemService.findTargetPopupIds());

        return new ItemReader<Long>() {
            @Override
            public synchronized Long read() {
                return delegate.read();
            }
        };
    }

    @Bean(name = UPDATE_ITEM_ANALYSIS_PROCESSOR)
    @StepScope
    public ItemProcessor<Long, List<Item>> updateItemAnalysisProcessor() {
        return itemService::processPopupItemAnalysis;
    }

    @Bean(name = UPDATE_ITEM_ANALYSIS_WRITER)
    @StepScope
    public ItemWriter<List<Item>> updateItemAnalysisWriter() {
        return chunk -> {
            List<Item> allItemAnalyses = new ArrayList<>();

            for (List<Item> itemList : chunk.getItems()) {
                if (itemList != null) {
                    allItemAnalyses.addAll(itemList);
                }
            }

            itemService.updateItemAnalysisList(allItemAnalyses);
        };
    }
}
