package com.lgcns.domain.paymentStats.service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PaymentStatsServiceTest extends IntegrationTest {

    @Autowired PaymentStatsService paymentStatsService;
    @Autowired PaymentStatsRepository paymentStatsRepository;
    @Autowired PopupRepository popupRepository;
    @Autowired ManagerRepository managerRepository;

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

            popupRepository.save(createTestPopup(manager, "popup1"));
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
            popupRepository.save(createTestPopup(otherManager, "popup2"));

            // when & then
            assertThatThrownBy(() -> paymentStatsService.findLatestAverageAmount(2L))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(PopupErrorCode.POPUP_UNAUTHORIZED.getMessage());
        }
    }

    private Popup createTestPopup(Manager manager, String name) {
        return Popup.createPopup(
                manager,
                name,
                "https://bucket/이미지.jpg",
                LocalDate.now().minusMonths(1),
                LocalDate.parse("2025-05-01"),
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
