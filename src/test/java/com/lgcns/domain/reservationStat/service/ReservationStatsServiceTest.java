package com.lgcns.domain.reservationStat.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.reservationStats.domain.DailyReservationCount;
import com.lgcns.domain.reservationStats.domain.WeekDayReservationCount;
import com.lgcns.domain.reservationStats.dto.response.DayOfWeek;
import com.lgcns.domain.reservationStats.dto.response.ReservationStatsResponse;
import com.lgcns.domain.reservationStats.dto.response.WeekDayReservationCountResponse;
import com.lgcns.domain.reservationStats.repository.DailyReservationCountRepository;
import com.lgcns.domain.reservationStats.repository.WeekDayReservationCountRepository;
import com.lgcns.domain.reservationStats.service.ReservationStatsService;
import com.lgcns.global.error.exception.CustomException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class ReservationStatsServiceTest extends IntegrationTest {
    @Autowired private ReservationStatsService reservationStatsService;

    @Autowired private ManagerRepository managerRepository;
    @Autowired private PopupRepository popupRepository;
    @Autowired private DailyReservationCountRepository dailyReservationCountRepository;
    @Autowired private WeekDayReservationCountRepository weekDayReservationCountRepository;

    private Manager ownerManager;
    private Manager otherManager;
    private Popup popup;

    @BeforeEach
    void setUp() {
        ownerManager =
                managerRepository.save(Manager.createManager("testUsername", "testPassword"));
        otherManager =
                managerRepository.save(Manager.createManager("otherManager", "testPassword"));

        setAuthentication(ownerManager);

        popup =
                Popup.createPopup(
                        ownerManager,
                        "testPopup",
                        "https://bucket/이미지.jpg",
                        LocalDate.parse("2025-01-01"),
                        LocalDate.parse("2025-01-31"),
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
        popup = popupRepository.save(popup);
    }

    @Nested
    class 예약_통계_조회 {

        @Test
        @Transactional
        void 예약자가_있는_경우_예약_통계_조회에_성공한다() {
            // given
            Long popupId = popup.getId();
            dailyReservationCountRepository.save(
                    DailyReservationCount.createDailyReservationCount(popupId, 5));

            weekDayReservationCountRepository.save(
                    WeekDayReservationCount.createWeekDayReservationCount(
                            popupId, 1, 1, 1, 1, 1, 0, 0));

            // when
            ReservationStatsResponse reservationStats =
                    reservationStatsService.getReservationStats(popupId);

            // then
            Assertions.assertAll(
                    () -> assertThat(reservationStats.reservedCount()).isEqualTo(5),
                    () -> assertThat(reservationStats.chart().size()).isEqualTo(7),
                    () ->
                            assertThat(reservationStats.chart().get(0))
                                    .isEqualTo(
                                            WeekDayReservationCountResponse.of(
                                                    DayOfWeek.MONDAY, 1)),
                    () ->
                            assertThat(reservationStats.chart().get(1))
                                    .isEqualTo(
                                            WeekDayReservationCountResponse.of(
                                                    DayOfWeek.TUESDAY, 1)),
                    () ->
                            assertThat(reservationStats.chart().get(2))
                                    .isEqualTo(
                                            WeekDayReservationCountResponse.of(
                                                    DayOfWeek.WEDNESDAY, 1)),
                    () ->
                            assertThat(reservationStats.chart().get(3))
                                    .isEqualTo(
                                            WeekDayReservationCountResponse.of(
                                                    DayOfWeek.THURSDAY, 1)),
                    () ->
                            assertThat(reservationStats.chart().get(4))
                                    .isEqualTo(
                                            WeekDayReservationCountResponse.of(
                                                    DayOfWeek.FRIDAY, 1)),
                    () ->
                            assertThat(reservationStats.chart().get(5))
                                    .isEqualTo(
                                            WeekDayReservationCountResponse.of(
                                                    DayOfWeek.SATURDAY, 0)),
                    () ->
                            assertThat(reservationStats.chart().get(6))
                                    .isEqualTo(
                                            WeekDayReservationCountResponse.of(
                                                    DayOfWeek.SUNDAY, 0)));
        }

        @Test
        @Transactional
        void 예약자가_없는_경우_예약_통계_조회에_성공한다() {
            // given
            Long popupId = popup.getId();

            // when
            ReservationStatsResponse reservationStats =
                    reservationStatsService.getReservationStats(popupId);

            // then
            Assertions.assertAll(
                    () -> assertThat(reservationStats.reservedCount()).isEqualTo(0),
                    () -> assertThat(reservationStats.chart().size()).isEqualTo(7),
                    () ->
                            assertThat(reservationStats.chart().get(0))
                                    .isEqualTo(
                                            WeekDayReservationCountResponse.of(
                                                    DayOfWeek.MONDAY, 0)),
                    () ->
                            assertThat(reservationStats.chart().get(1))
                                    .isEqualTo(
                                            WeekDayReservationCountResponse.of(
                                                    DayOfWeek.TUESDAY, 0)),
                    () ->
                            assertThat(reservationStats.chart().get(2))
                                    .isEqualTo(
                                            WeekDayReservationCountResponse.of(
                                                    DayOfWeek.WEDNESDAY, 0)),
                    () ->
                            assertThat(reservationStats.chart().get(3))
                                    .isEqualTo(
                                            WeekDayReservationCountResponse.of(
                                                    DayOfWeek.THURSDAY, 0)),
                    () ->
                            assertThat(reservationStats.chart().get(4))
                                    .isEqualTo(
                                            WeekDayReservationCountResponse.of(
                                                    DayOfWeek.FRIDAY, 0)),
                    () ->
                            assertThat(reservationStats.chart().get(5))
                                    .isEqualTo(
                                            WeekDayReservationCountResponse.of(
                                                    DayOfWeek.SATURDAY, 0)),
                    () ->
                            assertThat(reservationStats.chart().get(6))
                                    .isEqualTo(
                                            WeekDayReservationCountResponse.of(
                                                    DayOfWeek.SUNDAY, 0)));
        }

        @Test
        @Transactional
        void 다른_관리자가_소유한_팝업에_대한_예약_통계_조회는_실패한다() {
            // given
            Long popupId = popup.getId();
            setAuthentication(otherManager);

            // when & then
            Assertions.assertThrows(
                    CustomException.class,
                    () -> reservationStatsService.getReservationStats(popupId),
                    PopupErrorCode.POPUP_UNAUTHORIZED.getMessage());
        }

        @Test
        @Transactional
        void 존재하지_않는_팝업에_대한_예약_통계_조회는_실패한다() {
            // given
            Long popupId = -1L;

            // when & then
            Assertions.assertThrows(
                    CustomException.class,
                    () -> reservationStatsService.getReservationStats(popupId),
                    PopupErrorCode.POPUP_NOT_FOUND.getMessage());
        }
    }
}
