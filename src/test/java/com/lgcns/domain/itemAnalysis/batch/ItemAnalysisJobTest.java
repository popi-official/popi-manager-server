package com.lgcns.domain.itemAnalysis.batch;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.itemAnalysis.domain.ItemAnalysis;
import com.lgcns.domain.itemAnalysis.domain.ItemSalesStats;
import com.lgcns.domain.itemAnalysis.repository.ItemAnalysisRepository;
import com.lgcns.domain.itemAnalysis.repository.ItemSalesStatsRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.infra.dynamodb.itemAnalysis.PopupEventDynamoDbClient;
import com.lgcns.infra.dynamodb.itemAnalysis.PopupEventResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBatchTest
public class ItemAnalysisJobTest extends IntegrationTest {

    public static final String ITEM_ANALYSIS_JOB = "itemAnalysisJob";

    @Autowired private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired private ItemRepository itemRepository;
    @Autowired private PopupRepository popupRepository;
    @Autowired private ManagerRepository managerRepository;
    @Autowired private ItemSalesStatsRepository itemSalesStatsRepository;
    @Autowired private ItemAnalysisRepository itemAnalysisRepository;

    @MockitoBean private PopupEventDynamoDbClient dynamoDbClient;

    private Manager ownerManager;

    @Autowired
    @Qualifier(ITEM_ANALYSIS_JOB)
    Job itemAnalysisJob;

    @BeforeEach
    void setup() {
        jobLauncherTestUtils.setJob(itemAnalysisJob);

        ownerManager =
                managerRepository.save(Manager.createManager("ownerManager", "ownerPassword"));
        setAuthentication(ownerManager);
    }

    @Nested
    class item_analysis_job이_실행될_때 {
        @Test
        void 진행_중인_팝업이_없어도_실행에_성공한다() throws Exception {
            // given
            Popup closedPopup = createClosedPopup();
            popupRepository.save(closedPopup);

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
        void 진행_중인_팝업에_상품이_없어도_실행에_성공한다() throws Exception {
            // given
            Popup runningPopup = createRunningPopup();
            popupRepository.save(runningPopup);

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

        @Test
        void 진행_중인_팝업에_상품과_판매량_데이터가_있으면_분석_데이터를_생성한다() throws Exception {
            // given
            Popup runningPopup = createRunningPopup();
            popupRepository.save(runningPopup);
            Long popupId = runningPopup.getId();

            Item item1 =
                    Item.createItem(
                            runningPopup, "지수 포토카드", "https://bucket/jisoo.jpg", 5000, 50, 5, "a1");
            Item item2 =
                    Item.createItem(
                            runningPopup,
                            "제니 포토카드",
                            "https://bucket/jennie.jpg",
                            15000,
                            100,
                            10,
                            "a2");
            itemRepository.saveAll(List.of(item1, item2));

            // 판매량 데이터 준비
            itemSalesStatsRepository.saveAll(
                    List.of(
                            ItemSalesStats.createItemSalesStats(popupId, item1.getId(), 30),
                            ItemSalesStats.createItemSalesStats(popupId, item2.getId(), 20)));

            // DynamoDB 이벤트 모킹
            List<PopupEventResponse> mockEvents =
                    createMockPopupEvents(popupId, item1.getId(), item2.getId());
            when(dynamoDbClient.getEventsBetweenTimes(eq(popupId), any(), any()))
                    .thenReturn(mockEvents);

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

        @Test
        void 기존_분석_데이터가_있는_경우_업데이트한다() throws Exception {
            // given
            Popup runningPopup = createRunningPopup();
            popupRepository.save(runningPopup);
            Long popupId = runningPopup.getId();

            Item item1 =
                    Item.createItem(
                            runningPopup, "지수 포토카드", "https://bucket/jisoo.jpg", 5000, 50, 5, "a1");
            itemRepository.save(item1);

            // 기존 분석 데이터
            ItemAnalysis existingAnalysis = ItemAnalysis.createItemAnalysis(item1, 50, 0.0, 20);
            itemAnalysisRepository.save(existingAnalysis);

            // 판매량 데이터
            itemSalesStatsRepository.save(
                    ItemSalesStats.createItemSalesStats(popupId, item1.getId(), 35));

            List<PopupEventResponse> mockEvents = createMockPopupEvents(popupId, item1.getId());
            when(dynamoDbClient.getEventsBetweenTimes(eq(popupId), any(), any()))
                    .thenReturn(mockEvents);

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

            // 업데이트된 분석 데이터 검증
            ItemAnalysis updatedAnalysis =
                    itemAnalysisRepository.findByItemId(item1.getId()).orElseThrow();
            assertThat(updatedAnalysis.getPopularityScore()).isEqualTo(90); // 기존 50 + 새로운 40
            assertThat(updatedAnalysis.getSalesVolume()).isEqualTo(35);
        }

        @Test
        void DynamoDB_이벤트가_없어도_판매량_데이터로_분석_데이터를_생성한다() throws Exception {
            // given
            Popup runningPopup = createRunningPopup();
            popupRepository.save(runningPopup);
            Long popupId = runningPopup.getId();

            Item item1 =
                    Item.createItem(
                            runningPopup, "지수 포토카드", "https://bucket/jisoo.jpg", 5000, 50, 5, "a1");
            itemRepository.save(item1);

            itemSalesStatsRepository.save(
                    ItemSalesStats.createItemSalesStats(popupId, item1.getId(), 25));

            when(dynamoDbClient.getEventsBetweenTimes(eq(popupId), any(), any()))
                    .thenReturn(new ArrayList<>());

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

            ItemAnalysis savedAnalysis =
                    itemAnalysisRepository.findByItemId(item1.getId()).orElseThrow();
            assertThat(savedAnalysis.getPopularityScore()).isEqualTo(0);
            assertThat(savedAnalysis.getSalesVolume()).isEqualTo(25);
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
                "closedPopup",
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
                "runningPopup",
                "https://bucket/이미지.jpg",
                nowDate,
                nowDate.plusDays(5),
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

    private List<PopupEventResponse> createMockPopupEvents(Long popupId, Long... itemIds) {
        List<PopupEventResponse> events = new ArrayList<>();

        for (int i = 0; i < itemIds.length; i++) {
            PopupEventResponse event = new PopupEventResponse();
            event.setPopupId(popupId.toString());
            event.setEventKey("2025-01-10T" + (12 + i) + ":00:00Z#stay");
            event.setItemId(itemIds[i]);
            event.setScore(40 - (i * 10)); // 첫 번째 상품 40점, 두 번째 상품 30점
            events.add(event);
        }

        return events;
    }
}
