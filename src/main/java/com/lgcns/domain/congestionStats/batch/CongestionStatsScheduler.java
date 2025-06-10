package com.lgcns.domain.congestionStats.batch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CongestionStatsScheduler {

    public static final String CONGESTION_STATS_JOB = "congestionStatsJob";

    private final Job congestionStatsJob;
    private final JobLauncher jobLauncher;

    public CongestionStatsScheduler(
            @Qualifier(CONGESTION_STATS_JOB) Job congestionStatsJob, JobLauncher jobLauncher) {
        this.congestionStatsJob = congestionStatsJob;
        this.jobLauncher = jobLauncher;
    }

    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void createCongestionStats()
            throws JobInstanceAlreadyCompleteException,
                    JobExecutionAlreadyRunningException,
                    JobParametersInvalidException,
                    JobRestartException {

        String dateHour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH"));

        JobParameters jobParameters =
                new JobParametersBuilder().addString("dateHour", dateHour).toJobParameters();

        jobLauncher.run(congestionStatsJob, jobParameters);
    }
}
