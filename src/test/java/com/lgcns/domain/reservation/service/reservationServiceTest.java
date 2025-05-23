package com.lgcns.domain.reservation.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.dto.request.PopupCreateRequest;
import com.lgcns.domain.popup.dto.request.PopupWithChoicesCreateRequest;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.popup.service.PopupService;
import com.lgcns.domain.reservation.dto.response.MonthlyReservationResponse;
import com.lgcns.domain.survey.dto.request.ChoiceCreateRequest;
import com.lgcns.global.error.exception.CustomException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class reservationServiceTest extends IntegrationTest {

    @Autowired private ReservationService reservationService;
    @Autowired private PopupService popupService;
    @Autowired private PopupRepository popupRepository;
    @Autowired private ManagerRepository managerRepository;

    private Manager manager;
    private Popup popup;

    @BeforeEach
    void setUp() {
        manager = managerRepository.save(Manager.createManager("testUsername", "testPassword"));
        setAuthentication(manager);
        Long popupId = popupService.createPopup(createPopupWithChoicesCreateRequest()).popupId();

        popup = popupRepository.findById(popupId).get();
    }

    @Nested
    class 팝업에_대한_예약_날짜_조회 {
        @Test
        void 예약_날짜_조회_성공() {
            // given
            Long popupId = popup.getId();

            // when
            MonthlyReservationResponse monthlyReservationResponse =
                    reservationService.findReservationByIdAndDate(
                            popupId, YearMonth.from(popup.getPopupStartDate()).toString());

            // then
            Assertions.assertAll(
                    () ->
                            assertThat(monthlyReservationResponse.popupOpenDate())
                                    .isEqualTo(popup.getPopupStartDate()),
                    () ->
                            assertThat(monthlyReservationResponse.popupCloseDate())
                                    .isEqualTo(popup.getPopupEndDate()),
                    () ->
                            assertThat(monthlyReservationResponse.timeCapacity())
                                    .isEqualTo(popup.getTimeCapacity()),
                    () ->
                            assertThat(monthlyReservationResponse.dailyReservations().size())
                                    .isEqualTo(
                                            popup.getPopupEndDate()
                                                            .compareTo(popup.getPopupStartDate())
                                                    + 1),
                    () ->
                            assertThat(
                                            monthlyReservationResponse
                                                    .dailyReservations()
                                                    .get(0)
                                                    .reservationDate())
                                    .isEqualTo(popup.getPopupStartDate()),
                    () ->
                            assertThat(
                                            monthlyReservationResponse
                                                    .dailyReservations()
                                                    .get(0)
                                                    .timeSlots()
                                                    .size())
                                    .isEqualTo(
                                            popup.getRunCloseTime().getHour()
                                                    - popup.getRunOpenTime().getHour()
                                                    + 1));
        }

        @Test
        void 예약_날짜_조회_실패() {
            // given
            Long popupId = 999L; // 존재하지 않는 팝업 ID
            String date = "2023-10-01";

            // when & then
            assertThatThrownBy(() -> reservationService.findReservationByIdAndDate(popupId, date))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(PopupErrorCode.POPUP_NOT_FOUND.getMessage());
        }
    }

    private PopupWithChoicesCreateRequest createPopupWithChoicesCreateRequest() {
        return new PopupWithChoicesCreateRequest(
                new PopupCreateRequest(
                        "popup1",
                        "https://bucket/asdf",
                        LocalDate.parse("2025-12-01"),
                        LocalDate.parse("2025-12-31"),
                        LocalDateTime.parse("2025-12-01T10:00:00"),
                        LocalDateTime.parse("2025-12-31T20:00:00"),
                        LocalTime.parse("10:00:00"),
                        LocalTime.parse("20:00:00"),
                        100,
                        20,
                        "서울특별시 강남구 테헤란로 123",
                        "3층 A호",
                        37.123456,
                        127.123456),
                List.of(
                        new ChoiceCreateRequest(
                                List.of("choice1", "choice2", "choice3", "choice4")),
                        new ChoiceCreateRequest(
                                List.of("choice1", "choice2", "choice3", "choice4")),
                        new ChoiceCreateRequest(
                                List.of("choice1", "choice2", "choice3", "choice4")),
                        new ChoiceCreateRequest(
                                List.of("choice1", "choice2", "choice3", "choice4"))));
    }
}
