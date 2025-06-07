package com.lgcns.domain.itemAnalysis.batch;

import com.lgcns.domain.itemAnalysis.domain.ItemAnalysis;
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
public class ItemAnalysisJobManager {

    public static final Integer CHUNK_SIZE = 100;
    public static final Integer TASK_POOL_SIZE = 2;
    public static final String ITEM_ANALYSIS_JOB = "itemAnalysisJob";
    public static final String UPDATE_ITEM_ANALYSIS_STEP = "updateItemAnalysisStep";
    public static final String ITEM_ANALYSIS_TASK_EXECUTOR = "itemAnalysisTaskExecutor";

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
            @Qualifier(ItemAnalysisStepManager.UPDATE_ITEM_ANALYSIS_ITEM_READER)
                    ItemReader<Long> updateItemAnalysisItemReader,
            @Qualifier(ItemAnalysisStepManager.UPDATE_ITEM_ANALYSIS_ITEM_PROCESSOR)
                    ItemProcessor<Long, List<ItemAnalysis>> updateItemAnalysisItemProcessor,
            @Qualifier(ItemAnalysisStepManager.UPDATE_ITEM_ANALYSIS_ITEM_WRITER)
                    ItemWriter<List<ItemAnalysis>> updateItemAnalysisItemWriter,
            @Qualifier(ITEM_ANALYSIS_TASK_EXECUTOR) TaskExecutor taskExecutor) {
        return new StepBuilder(UPDATE_ITEM_ANALYSIS_STEP, jobRepository)
                .<Long, List<ItemAnalysis>>chunk(CHUNK_SIZE, transactionManager)
                .reader(updateItemAnalysisItemReader)
                .processor(updateItemAnalysisItemProcessor)
                .writer(updateItemAnalysisItemWriter)
                .faultTolerant()
                .retryLimit(3)
                .retry(DataAccessException.class)
                .skipLimit(3)
                .skip(CustomException.class)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean(name = ITEM_ANALYSIS_TASK_EXECUTOR)
    public TaskExecutor itemAnalysisTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(TASK_POOL_SIZE);
        taskExecutor.setMaxPoolSize(TASK_POOL_SIZE * 2);
        taskExecutor.setThreadNamePrefix("async-thread");
        return taskExecutor;
    }
}
