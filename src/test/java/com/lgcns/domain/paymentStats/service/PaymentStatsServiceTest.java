package com.lgcns.domain.paymentStats.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.paymentStats.domain.PaymentStats;
import com.lgcns.domain.paymentStats.dto.response.PaymentAverageResponse;
import com.lgcns.domain.paymentStats.exception.PaymentStatsErrorCode;
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
                        LocalDate.parse("2025-06-15"),
                        LocalDateTime.of(LocalDate.now().minusMonths(1), LocalTime.of(6, 0)),
                        LocalDateTime.parse("2025-06-15T22:00:00"),
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
                        popup, LocalDate.of(2025, 5, 15), LocalTime.of(8, 0), 1250000, 124));
        paymentStatsRepository.save(
                PaymentStats.createPaymentStats(
                        popup, LocalDate.of(2025, 5, 15), LocalTime.of(10, 0), 2345000, 156));
        paymentStatsRepository.save(
                PaymentStats.createPaymentStats(
                        popup, LocalDate.of(2025, 5, 15), LocalTime.of(12, 0), 3560000, 187));
        paymentStatsRepository.save(
                PaymentStats.createPaymentStats(
                        popup, LocalDate.of(2025, 5, 15), LocalTime.of(14, 0), 4250000, 195));

        // 오늘 날짜로 가정한 데이터 추가
        LocalDate today = LocalDate.now();
        paymentStatsRepository.save(
                PaymentStats.createPaymentStats(popup, today, LocalTime.of(8, 0), 980000, 98));
        paymentStatsRepository.save(
                PaymentStats.createPaymentStats(popup, today, LocalTime.of(10, 0), 1870000, 145));
        paymentStatsRepository.save(
                PaymentStats.createPaymentStats(popup, today, LocalTime.of(12, 0), 4120000, 194));
    }

    @Nested
    @DisplayName("평균 결제액 조회 테스트")
    class GetPaymentAverageTest {

        @Test
        @Transactional
        @DisplayName("정상적으로 평균 결제액 조회에 성공한다")
        void getPaymentAverage_Success() {
            // given
            Long popupId = popup.getId();

            // when
            PaymentAverageResponse response = paymentStatsService.getPaymentAverage(popupId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.totalPrice()).isNotNull();
            assertThat(response.todayPrice()).isNotNull();
        }

        @Test
        @Transactional
        @DisplayName("존재하지 않는 팝업 ID로 조회하면 예외가 발생한다")
        void getPaymentAverage_PopupNotFound() {
            // given
            Long nonExistentPopupId = 9999L;

            // when & then
            assertThatThrownBy(() -> paymentStatsService.getPaymentAverage(nonExistentPopupId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_NOT_FOUND);
        }

        @Test
        @Transactional
        @DisplayName("다른 매니저의 팝업에 접근하면 예외가 발생한다")
        void getPaymentAverage_Unauthorized() {
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
            assertThatThrownBy(() -> paymentStatsService.getPaymentAverage(otherPopupId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_UNAUTHORIZED);
        }

        @Test
        @Transactional
        @DisplayName("팝업의 시작일이 현재 날짜보다 미래인 경우 예외가 발생한다")
        void getPaymentAverage_InvalidDateRange() {
            // given
            LocalDate futureDate = LocalDate.now().plusDays(7); // 미래 날짜로 설정
            Popup futurePopup =
                    Popup.createPopup(
                            ownerManager,
                            "futurePopup",
                            "https://bucket/future.jpg",
                            futureDate,
                            futureDate.plusDays(30),
                            LocalDateTime.of(futureDate, LocalTime.of(6, 0)),
                            LocalDateTime.of(futureDate.plusDays(30), LocalTime.of(22, 0)),
                            LocalTime.parse("06:00:00"),
                            LocalTime.parse("22:00:00"),
                            100,
                            20,
                            "서울특별시 강남구 테헤란로 888",
                            "8층 8호",
                            37.888888,
                            127.888888);
            futurePopup = popupRepository.save(futurePopup);
            final Long futurePopupId = futurePopup.getId();

            // when & then
            assertThatThrownBy(() -> paymentStatsService.getPaymentAverage(futurePopupId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode", PaymentStatsErrorCode.INVALID_DATE_RANGE);
        }

        @Test
        @Transactional
        @DisplayName("계산된 1인당 구매 평균값이 정확한지 확인한다")
        void getPaymentAverage_CorrectCalculation() {
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
                            popup, today.minusDays(1), LocalTime.of(12, 0), 40000, 10));
            // 오늘
            paymentStatsRepository.save(
                    PaymentStats.createPaymentStats(popup, today, LocalTime.of(12, 0), 60000, 10));

            // when
            PaymentAverageResponse response = paymentStatsService.getPaymentAverage(popupId);

            // then
            assertThat(response.totalPrice()).isEqualTo("5000"); // 5000
            assertThat(response.todayPrice()).isEqualTo("6000"); // 6000
        }
    }
}
