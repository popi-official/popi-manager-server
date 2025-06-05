package com.lgcns.domain.paymentStats.service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgcns.IntegrationTest;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.paymentStats.domain.AveragePeriod;
import com.lgcns.domain.paymentStats.domain.PaymentStats;
import com.lgcns.domain.paymentStats.dto.response.AverageAmountResponse;
import com.lgcns.domain.paymentStats.repository.PaymentStatsRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PaymentStatsServiceTest extends IntegrationTest {

    @Autowired PaymentStatsService paymentStatsService;
    @Autowired PaymentStatsRepository paymentStatsRepository;
    @Autowired PopupRepository popupRepository;
    @Autowired ManagerRepository managerRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Manager manager;
    private Manager otherManager;

    @Nested
    class 평균_구매액을_조회할_때 {

        @BeforeEach
        void setUp() {
            manager = managerRepository.save(Manager.createManager("testManager1", "testPassword"));
            otherManager =
                    managerRepository.save(Manager.createManager("otherManager", "testPassword"));

            setAuthentication(manager);

            popupRepository.save(
                    createTestPopup(
                            manager, "popup1", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 7, 1)));
            paymentStatsRepository.saveAll(
                    List.of(
                            createTestPaymentStats(50000, AveragePeriod.TOTAL),
                            createTestPaymentStats(60000, AveragePeriod.TODAY)));
        }

        @Test
        void 정상적으로_평균_구매액_조회에_성공한다() {
            // given
            Long popupId = 1L;

            // when
            AverageAmountResponse response = paymentStatsService.findLatestAverageAmount(popupId);

            // then
            assertThat(response.totalAverageAmount()).isEqualTo(50000);
            assertThat(response.todayAverageAmount()).isEqualTo(60000);
        }

        @Test
        void 데이터가_존재하지_않으면_0을_반환한다() {
            // given
            paymentStatsRepository.deleteAll();

            // when
            AverageAmountResponse response = paymentStatsService.findLatestAverageAmount(1L);

            // then
            assertThat(response.totalAverageAmount()).isEqualTo(0);
            assertThat(response.todayAverageAmount()).isEqualTo(0);
        }

        @Test
        void 존재하지_않는_팝업_ID로_조회하면_예외가_발생한다() {
            // when & then
            assertThatThrownBy(() -> paymentStatsService.findLatestAverageAmount(999L))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(PopupErrorCode.POPUP_NOT_FOUND.getMessage());
        }

        @Test
        void 다른_매니저의_팝업에_접근하면_예외가_발생한다() {
            // given
            popupRepository.save(
                    createTestPopup(
                            otherManager,
                            "popup2",
                            LocalDate.of(2025, 6, 1),
                            LocalDate.of(2025, 7, 1)));

            // when & then
            assertThatThrownBy(() -> paymentStatsService.findLatestAverageAmount(2L))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(PopupErrorCode.POPUP_UNAUTHORIZED.getMessage());
        }
    }

    @Nested
    class 전체_팝업_평균_구매액을_저장할_때 {

        @BeforeEach
        void setUp() {
            manager = managerRepository.save(Manager.createManager("testManager1", "testPassword"));
            popupRepository.saveAll(
                    List.of(
                            createTestPopup(
                                    manager,
                                    "popup1",
                                    LocalDate.of(2025, 6, 1),
                                    LocalDate.of(2025, 7, 1)),
                            createTestPopup(
                                    manager,
                                    "popup2",
                                    LocalDate.of(2025, 5, 1),
                                    LocalDate.of(2025, 6, 1))));
        }

        @Test
        void 운영_중인_팝업만_통계_데이터가_저장된다() throws JsonProcessingException {
            // given
            String expectedResponse1 =
                    objectMapper.writeValueAsString(
                            Map.of("totalAverageAmount", 300_000, "todayAverageAmount", 100_000));

            stubFor(
                    get(urlEqualTo("/internal/1/average-purchase"))
                            .willReturn(
                                    aResponse()
                                            .withStatus(200)
                                            .withHeader("Content-Type", "application/json")
                                            .withBody(expectedResponse1)));

            String expectedResponse2 =
                    objectMapper.writeValueAsString(
                            Map.of("totalAverageAmount", 200_000, "todayAverageAmount", 150_000));

            stubFor(
                    get(urlEqualTo("/internal/2/average-purchase"))
                            .willReturn(
                                    aResponse()
                                            .withStatus(200)
                                            .withHeader("Content-Type", "application/json")
                                            .withBody(expectedResponse2)));

            // when
            paymentStatsService.createPaymentStats();

            // then
            List<PaymentStats> stats = paymentStatsRepository.findAll();

            assertThat(stats).hasSize(2);
            assertThat(stats)
                    .extracting(
                            PaymentStats::getPopupId,
                            PaymentStats::getAverageAmount,
                            PaymentStats::getPeriod)
                    .containsExactlyInAnyOrder(
                            tuple(1L, 300_000, AveragePeriod.TOTAL),
                            tuple(1L, 100_000, AveragePeriod.TODAY));
        }
    }

    private Popup createTestPopup(
            Manager manager, String name, LocalDate popupStartDate, LocalDate popupEndDate) {
        return Popup.createPopup(
                manager,
                name,
                "https://bucket/이미지.jpg",
                popupStartDate,
                popupEndDate,
                LocalDateTime.of(LocalDate.now().minusMonths(1), LocalTime.of(6, 0)),
                LocalDateTime.parse("2025-05-01T22:00:00"),
                LocalTime.parse("06:00:00"),
                LocalTime.parse("22:00:00"),
                100,
                20,
                "서울특별시 강남구 테헤란로 123",
                "3층 A호",
                37.123456,
                127.123456);
    }

    private PaymentStats createTestPaymentStats(int averageAmount, AveragePeriod period) {
        return PaymentStats.createPaymentStats(
                1L, averageAmount, period, LocalDate.now(), LocalTime.now());
    }
}
