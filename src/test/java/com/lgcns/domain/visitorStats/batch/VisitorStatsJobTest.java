package com.lgcns.domain.visitorStats.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.entrance.domain.Entrance;
import com.lgcns.domain.entrance.domain.MemberAge;
import com.lgcns.domain.entrance.domain.MemberGender;
import com.lgcns.domain.entrance.repository.EntranceRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.repository.PopupRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@SpringBatchTest
public class VisitorStatsJobTest extends IntegrationTest {

    public static final String VISITOR_STATS_JOB = "visitorStatsJob";

    @Autowired private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired private PopupRepository popupRepository;

    @Autowired private ManagerRepository managerRepository;

    @Autowired private EntranceRepository entranceRepository;

    private Manager ownerManager;

    @Autowired
    @Qualifier(VISITOR_STATS_JOB)
    Job visitorStatsJob;

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(visitorStatsJob);

        ownerManager =
                managerRepository.save(Manager.createManager("ownerManager", "ownerPassword"));

        setAuthentication(ownerManager);
    }

    @Nested
    class visitor_stats_job이_실행될_때 {

        @Test
        void 진행_중인_팝업이_없어도_실행에_성공한다() throws Exception {
            // given
            Popup popup = createClosedPopup();
            popupRepository.save(popup);

            JobParameters jobParameters = buildJobParameters();

            // when
            JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
            StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();

            // then
            assertAll(
                    () -> assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED),
                    () -> assertThat(stepExecution.getReadCount()).isEqualTo(0),
                    () -> assertThat(stepExecution.getWriteCount()).isEqualTo(0),
                    () -> assertThat(stepExecution.getSkipCount()).isEqualTo(0));
        }

        @Test
        void 진행_중인_팝업에_입장_정보가_없어도_실행에_성공한다() throws Exception {
            // given
            Popup popup = createRunningPopup();
            popupRepository.save(popup);

            JobParameters jobParameters = buildJobParameters();

            // when
            JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
            StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();

            // then
            assertAll(
                    () -> assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED),
                    () -> assertThat(stepExecution.getReadCount()).isEqualTo(0),
                    () -> assertThat(stepExecution.getWriteCount()).isEqualTo(0),
                    () -> assertThat(stepExecution.getSkipCount()).isEqualTo(0));
        }

        @Test
        void 진행_중인_팝업에_한_시간_전_입장_정보가_없어_예외가_발생하면_Step은_스킵되고_Job_실행은_성공한다() throws Exception {
            // given
            Popup popup = createRunningPopup();
            popupRepository.save(popup);
            Long popupId = popup.getId();

            LocalDate nowDate = LocalDate.now();
            LocalTime nowTime = LocalTime.now();

            entranceRepository.save(
                    Entrance.createPopupEnter(
                            popupId,
                            MemberGender.MALE,
                            MemberAge.TWENTIES,
                            nowDate,
                            nowTime.minusHours(2)));
            entranceRepository.save(
                    Entrance.createPopupEnter(
                            popupId,
                            MemberGender.MALE,
                            MemberAge.TWENTIES,
                            nowDate,
                            nowTime.minusHours(2)));
            entranceRepository.save(
                    Entrance.createPopupEnter(
                            popupId,
                            MemberGender.MALE,
                            MemberAge.TWENTIES,
                            nowDate,
                            nowTime.minusHours(2)));

            JobParameters jobParameters = buildJobParameters();

            // when
            JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
            StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();

            // then
            assertAll(
                    () -> assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED),
                    () -> assertThat(stepExecution.getReadCount()).isEqualTo(1),
                    () -> assertThat(stepExecution.getWriteCount()).isEqualTo(0),
                    /*() -> {
                        List<Throwable> exceptions = stepExecution.getFailureExceptions();
                        assertThat(exceptions).hasSize(1);
                        assertThat(exceptions.get(0)).isInstanceOf(CustomException.class);
                        CustomException exception = (CustomException) exceptions.get(0);
                        assertThat(exception.getErrorCode()).isEqualTo(VisitorStatsErrorCode.HOURLY_ENTRANCE_NOT_FOUND);
                    },*/
                    // 예외를 받으면 Step을 스킵하기 때문에 getFailureExceptions() 메서드로 실패에 대한 예외 조회 불가능
                    () -> assertThat(stepExecution.getSkipCount()).isEqualTo(1));
        }

        @Test
        void 진행_중인_팝업에_한_시간_전_입장_정보가_있으면_실행에_성공한다() throws Exception {
            // given
            Popup popup = createRunningPopup();
            popupRepository.save(popup);
            Long popupId = popup.getId();

            LocalDate nowDate = LocalDate.now();
            LocalTime nowTime = LocalTime.now();

            entranceRepository.save(
                    Entrance.createPopupEnter(
                            popupId,
                            MemberGender.MALE,
                            MemberAge.TWENTIES,
                            nowDate,
                            nowTime.minusHours(1).truncatedTo(ChronoUnit.HOURS)));
            entranceRepository.save(
                    Entrance.createPopupEnter(
                            popupId,
                            MemberGender.MALE,
                            MemberAge.TWENTIES,
                            nowDate,
                            nowTime.minusHours(1).truncatedTo(ChronoUnit.HOURS)));
            entranceRepository.save(
                    Entrance.createPopupEnter(
                            popupId,
                            MemberGender.MALE,
                            MemberAge.TWENTIES,
                            nowDate,
                            nowTime.minusHours(1).truncatedTo(ChronoUnit.HOURS)));

            JobParameters jobParameters = buildJobParameters();

            // when
            JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
            StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();

            // then
            assertAll(
                    () -> assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED),
                    () -> assertThat(stepExecution.getReadCount()).isEqualTo(1),
                    () -> assertThat(stepExecution.getWriteCount()).isEqualTo(1),
                    () -> assertThat(stepExecution.getSkipCount()).isEqualTo(0));
        }
    }

    private JobParameters buildJobParameters() {
        return new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
    }

    private Popup createClosedPopup() {
        return Popup.createPopup(
                ownerManager,
                "testPopup",
                "https://bucket/이미지.jpg",
                LocalDate.parse("2020-01-01"),
                LocalDate.parse("2020-01-31"),
                LocalDateTime.parse("2025-01-01T10:00:00"),
                LocalDateTime.parse("2025-01-31T20:00:00"),
                LocalTime.parse("10:00:00"),
                LocalTime.parse("20:00:00"),
                100,
                20,
                "서울특별시 강남구 테헤란로 123",
                "3층 A호",
                37.123456,
                127.123456);
    }

    private Popup createRunningPopup() {
        LocalDate nowDate = LocalDate.now();

        return Popup.createPopup(
                ownerManager,
                "testPopup",
                "https://bucket/이미지.jpg",
                nowDate,
                nowDate.plusMonths(5),
                LocalDateTime.parse("2025-01-01T10:00:00"),
                LocalDateTime.parse("2025-01-31T20:00:00"),
                LocalTime.parse("00:00:00"),
                LocalTime.parse("23:59:59"),
                100,
                20,
                "서울특별시 강남구 테헤란로 123",
                "3층 A호",
                37.123456,
                127.123456);
    }
}
