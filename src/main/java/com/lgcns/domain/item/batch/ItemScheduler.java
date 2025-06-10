package com.lgcns.domain.item.batch;

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
public class ItemScheduler {

    public static final String ITEM_ANALYSIS_JOB = "itemAnalysisJob";

    private final JobLauncher jobLauncher;
    private final Job itemJob;

    public ItemScheduler(
            @Qualifier(ITEM_ANALYSIS_JOB) Job itemAnalysisJob, JobLauncher jobLauncher) {
        this.itemJob = itemAnalysisJob;
        this.jobLauncher = jobLauncher;
    }

    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void updateItemAnalysis()
            throws JobInstanceAlreadyCompleteException,
                    JobExecutionAlreadyRunningException,
                    JobParametersInvalidException,
                    JobRestartException {

        String dateHour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH"));

        JobParameters jobParameters =
                new JobParametersBuilder().addString("dateHour", dateHour).toJobParameters();

        jobLauncher.run(itemJob, jobParameters);
    }
}
