package com.lgcns.infra.batch;

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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class VisitorStatsJobConfig {

    public static final Integer CHUNK_SIZE = 100;
    public static final String VISITOR_STATS_JOB = "visitorStatsJob";
    public static final String CREATE_VISITOR_STATS_STEP = "createVisitorStatsStep";
    public static final String VISITOR_STATS_TASK_EXECUTOR = "visitorStatsTaskExecutor";

    @Bean(name = VISITOR_STATS_JOB)
    public Job visitorStatsJob(JobRepository jobRepository, Step createVisitorStatsStep) {
        return new JobBuilder(VISITOR_STATS_JOB, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(createVisitorStatsStep)
                .build();
    }

    @Bean(name = CREATE_VISITOR_STATS_STEP)
    @JobScope
    public Step createVisitorStatsStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<Long> createVisitorStatsItemReader,
            ItemProcessor<Long, VisitorStats> createVisitorStatsItemProcessor,
            ItemWriter<VisitorStats> createVisitorStatsItemWriter,
            @Qualifier(VISITOR_STATS_TASK_EXECUTOR) TaskExecutor taskExecutor) {
        return new StepBuilder(CREATE_VISITOR_STATS_STEP, jobRepository)
                .<Long, VisitorStats>chunk(CHUNK_SIZE, transactionManager)
                .reader(createVisitorStatsItemReader)
                .processor(createVisitorStatsItemProcessor)
                .writer(createVisitorStatsItemWriter)
                .faultTolerant()
                .skip(CustomException.class)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean(name = VISITOR_STATS_TASK_EXECUTOR)
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(CHUNK_SIZE);
        taskExecutor.setMaxPoolSize(CHUNK_SIZE * 2);
        taskExecutor.setThreadNamePrefix("async-thread");
        return taskExecutor;
    }
}
