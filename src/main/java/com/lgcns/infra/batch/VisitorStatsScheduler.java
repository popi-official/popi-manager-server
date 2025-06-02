package com.lgcns.infra.batch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VisitorStatsScheduler {

    private final Job visitorStatsJob;
    private final JobLauncher jobLauncher;

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
