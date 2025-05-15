package com.lgcns.domain.paymentStats.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.paymentStats.domain.PaymentStats;
import com.lgcns.domain.paymentStats.dto.response.PaymentAverageResponse;
import com.lgcns.domain.paymentStats.repository.PaymentStatsRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class PaymentStatsServiceTest extends IntegrationTest {
    @Autowired PaymentStatsService paymentStatsService;
    @Autowired PaymentStatsRepository paymentStatsRepository;
    @Autowired PopupRepository popupRepository;
    @Autowired ManagerRepository managerRepository;

    private Manager ownerManager;
    private Manager otherManager;
    private Popup popup;

    @BeforeEach
    void setUp() {
        ownerManager =
                managerRepository.save(Manager.createManager("testManager1", "testPassword"));
        otherManager =
                managerRepository.save(Manager.createManager("otherManager", "testPassword"));

        setAuthentication(ownerManager);

        popup =
                Popup.createPopup(
                        ownerManager,
                        "testPopup",
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
        popup = popupRepository.save(popup);

        createTestPaymentStats();
    }

    private void createTestPaymentStats() {
        // 팝업 1 결제 통계 데이터
        paymentStatsRepository.save(
                PaymentStats.createPaymentStats(
                        popup.getId(), LocalDate.of(2025, 5, 2), LocalTime.of(8, 0), 1250000, 124));
        paymentStatsRepository.save(
                PaymentStats.createPaymentStats(
                        popup.getId(),
                        LocalDate.of(2025, 5, 2),
                        LocalTime.of(10, 0),
                        2345000,
                        156));
        paymentStatsRepository.save(
                PaymentStats.createPaymentStats(
                        popup.getId(),
                        LocalDate.of(2025, 5, 2),
                        LocalTime.of(12, 0),
                        3560000,
                        187));
        paymentStatsRepository.save(
                PaymentStats.createPaymentStats(
                        popup.getId(),
                        LocalDate.of(2025, 5, 3),
                        LocalTime.of(14, 0),
                        4250000,
                        195));

        // 오늘 날짜로 가정한 데이터 추가
        LocalDate today = LocalDate.now();
        paymentStatsRepository.save(
                PaymentStats.createPaymentStats(
                        popup.getId(), today, LocalTime.of(8, 0), 980000, 98));
        paymentStatsRepository.save(
                PaymentStats.createPaymentStats(
                        popup.getId(), today, LocalTime.of(10, 0), 1870000, 145));
        paymentStatsRepository.save(
                PaymentStats.createPaymentStats(
                        popup.getId(), today, LocalTime.of(12, 0), 4120000, 194));
    }

    @Nested
    @DisplayName("평균 결제액 조회 테스트")
    class GetPaymentAverageTest {

        @Test
        @Transactional
        void 정상적으로_평균_결제액_조회에_성공한다() {
            // given
            Long popupId = popup.getId();

            // when
            PaymentAverageResponse response = paymentStatsService.getPaymentAverages(popupId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.totalPrice()).isNotNull();
            assertThat(response.todayPrice()).isNotNull();
        }

        @Test
        @Transactional
        void 존재하지_않는_팝업_ID로_조회하면_예외가_발생한다() {
            // given
            Long nonExistentPopupId = 9999L;

            // when & then
            assertThatThrownBy(() -> paymentStatsService.getPaymentAverages(nonExistentPopupId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_NOT_FOUND);
        }

        @Test
        @Transactional
        void 다른_매니저의_팝업에_접근하면_예외가_발생한다() {
            // given
            Popup otherPopup =
                    Popup.createPopup(
                            otherManager,
                            "testPopup2",
                            "https://bucket/이미지2.jpg",
                            LocalDate.now().minusMonths(1),
                            LocalDate.parse("2025-07-16"),
                            LocalDateTime.of(LocalDate.now().minusMonths(1), LocalTime.of(6, 0)),
                            LocalDateTime.parse("2025-07-16T22:00:00"),
                            LocalTime.parse("06:00:00"),
                            LocalTime.parse("22:00:00"),
                            200,
                            30,
                            "인천광역시 서구 비즈니스로 123",
                            "16층 1601호",
                            39.123456,
                            129.123456);
            otherPopup = popupRepository.save(otherPopup);
            final Long otherPopupId = otherPopup.getId();

            // when & then
            assertThatThrownBy(() -> paymentStatsService.getPaymentAverages(otherPopupId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_UNAUTHORIZED);
        }

        @Test
        @Transactional
        void 계산된_1인당_구매_평균값이_정확한지_확인한다() {
            // given
            Long popupId = popup.getId();

            paymentStatsRepository.deleteAll();

            // 단순한 데이터 시나리오 구성
            // 전체 기간: 결제액 100,000원, 방문자 20명 -> 1인당 5,000원
            // 오늘: 결제액 60,000원, 방문자 10명 -> 1인당 6,000원
            LocalDate today = LocalDate.now();

            // 초기
            paymentStatsRepository.save(
                    PaymentStats.createPaymentStats(
                            popup.getId(), today.minusDays(1), LocalTime.of(12, 0), 40000, 10));
            // 오늘
            paymentStatsRepository.save(
                    PaymentStats.createPaymentStats(
                            popup.getId(), today, LocalTime.of(12, 0), 60000, 10));

            // when
            PaymentAverageResponse response = paymentStatsService.getPaymentAverages(popupId);

            // then
            assertThat(response.totalPrice()).isEqualTo(5000);
            assertThat(response.todayPrice()).isEqualTo(6000);
        }

        @Test
        @Transactional
        void 데이터가_없을_경우_0을_반환한다() {
            // given
            Long popupId = popup.getId();
            paymentStatsRepository.deleteAll();

            // when
            PaymentAverageResponse response = paymentStatsService.getPaymentAverages(popupId);

            // then
            assertThat(response.totalPrice()).isEqualTo(0);
            assertThat(response.todayPrice()).isEqualTo(0);
        }
    }
}
