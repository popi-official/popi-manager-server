package com.lgcns.domain.visitorStats.batch;

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
public class VisitorStatsScheduler {

    public static final String VISITOR_STATS_JOB = "visitorStatsJob";

    private final Job visitorStatsJob;
    private final JobLauncher jobLauncher;

    public VisitorStatsScheduler(
            @Qualifier(VISITOR_STATS_JOB) Job visitorStatsJob, JobLauncher jobLauncher) {
        this.visitorStatsJob = visitorStatsJob;
        this.jobLauncher = jobLauncher;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void createVisitorStats()
            throws JobInstanceAlreadyCompleteException,
                    JobExecutionAlreadyRunningException,
                    JobParametersInvalidException,
                    JobRestartException {

        String dateHour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH"));

        JobParameters jobParameters =
                new JobParametersBuilder().addString("dateHour", dateHour).toJobParameters();

        jobLauncher.run(visitorStatsJob, jobParameters);
    }
}
