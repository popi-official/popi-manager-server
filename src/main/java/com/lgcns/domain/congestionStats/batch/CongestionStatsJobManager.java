package com.lgcns.domain.congestionStats.batch;

import com.lgcns.domain.congestionStats.domain.CongestionStats;
import com.lgcns.domain.visitorStats.domain.VisitorStats;
import com.lgcns.global.error.exception.CustomException;
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
public class CongestionStatsJobManager {

    public static final Integer CHUNK_SIZE = 100;
    public static final Integer TASK_POOL_SIZE = 2;
    public static final String CONGESTION_STATS_JOB = "congestionStatsJob";
    public static final String CREATE_CONGESTION_STATS_STEP = "createCongestionStatsStep";
    public static final String CONGESTION_STATS_TASK_EXECUTOR = "congestionStatsTaskExecutor";

    private static final String CREATE_CONGESTION_STATS_ITEM_READER =
            "createCongestionStatsItemReader";
    private static final String CREATE_CONGESTION_STATS_ITEM_PROCESSOR =
            "createCongestionStatsItemProcessor";
    private static final String CREATE_CONGESTION_STATS_ITEM_WRITER =
            "createCongestionStatsItemWriter";

    @Bean(name = CONGESTION_STATS_JOB)
    public Job congestionStatsJob(JobRepository jobRepository, Step createCongestionStatsStep) {
        return new JobBuilder(CONGESTION_STATS_JOB, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(createCongestionStatsStep)
                .build();
    }

    @Bean(name = CREATE_CONGESTION_STATS_STEP)
    @JobScope
    public Step createCongestionStatsStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            @Qualifier(CREATE_CONGESTION_STATS_ITEM_READER)
                    ItemReader<Long> createCongestionStatsItemReader,
            @Qualifier(CREATE_CONGESTION_STATS_ITEM_PROCESSOR)
                    ItemProcessor<Long, CongestionStats> createCongestonStatsItemProcessor,
            @Qualifier(CREATE_CONGESTION_STATS_ITEM_WRITER)
                    ItemWriter<CongestionStats> createCongestionStatsItemWriter,
            @Qualifier(CONGESTION_STATS_TASK_EXECUTOR) TaskExecutor taskExecutor) {
        return new StepBuilder(CREATE_CONGESTION_STATS_STEP, jobRepository)
                .<Long, VisitorStats>chunk(CHUNK_SIZE, transactionManager)
                .reader(createCongestionStatsItemReader)
                .processor(createCongestonStatsItemProcessor)
                .writer(createCongestionStatsItemWriter)
                .faultTolerant()
                .retryLimit(3)
                .retry(DataAccessException.class)
                .skipLimit(3)
                .skip(CustomException.class)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean(name = CONGESTION_STATS_TASK_EXECUTOR)
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(TASK_POOL_SIZE);
        taskExecutor.setMaxPoolSize(TASK_POOL_SIZE * 2);
        taskExecutor.setThreadNamePrefix("async-thread");
        return taskExecutor;
    }
}
