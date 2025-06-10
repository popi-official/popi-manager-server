package com.lgcns.domain.item.batch;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.global.error.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ItemJobManager {

    public static final Integer CHUNK_SIZE = 100;
    public static final Integer TASK_POOL_SIZE = 2;
    public static final String ITEM_ANALYSIS_JOB = "itemAnalysisJob";
    public static final String UPDATE_ITEM_ANALYSIS_STEP = "updateItemAnalysisStep";
    public static final String ITEM_TASK_EXECUTOR = "itemTaskExecutor";

    @Bean(name = ITEM_ANALYSIS_JOB)
    public Job itemAnalysisJob(
            JobRepository jobRepository,
            @Qualifier(UPDATE_ITEM_ANALYSIS_STEP) Step updateItemAnalysisStep) {
        return new JobBuilder(ITEM_ANALYSIS_JOB, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(updateItemAnalysisStep)
                .build();
    }

    @Bean(name = UPDATE_ITEM_ANALYSIS_STEP)
    @JobScope
    public Step updateItemAnalysisStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            @Qualifier(ItemStepManager.UPDATE_ITEM_ANALYSIS_READER)
                    ItemReader<Long> updateItemAnalysisReader,
            @Qualifier(ItemStepManager.UPDATE_ITEM_ANALYSIS_PROCESSOR)
                    ItemProcessor<Long, List<Item>> updateItemAnalysisProcessor,
            @Qualifier(ItemStepManager.UPDATE_ITEM_ANALYSIS_WRITER)
                    ItemWriter<List<Item>> updateItemAnalysisWriter,
            @Qualifier(ITEM_TASK_EXECUTOR) TaskExecutor taskExecutor) {
        return new StepBuilder(UPDATE_ITEM_ANALYSIS_STEP, jobRepository)
                .<Long, List<Item>>chunk(CHUNK_SIZE, transactionManager)
                .reader(updateItemAnalysisReader)
                .processor(updateItemAnalysisProcessor)
                .writer(updateItemAnalysisWriter)
                .faultTolerant()
                .retryLimit(3)
                .retry(DataAccessException.class)
                .skipLimit(3)
                .skip(CustomException.class)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean(name = ITEM_TASK_EXECUTOR)
    public TaskExecutor itemTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(TASK_POOL_SIZE);
        taskExecutor.setMaxPoolSize(TASK_POOL_SIZE * 2);
        taskExecutor.setThreadNamePrefix("async-thread");
        return taskExecutor;
    }
}
