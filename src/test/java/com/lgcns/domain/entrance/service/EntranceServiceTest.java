package com.lgcns.domain.entrance.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.entrance.domain.Entrance;
import com.lgcns.domain.entrance.domain.MemberAge;
import com.lgcns.domain.entrance.domain.MemberGender;
import com.lgcns.domain.entrance.dto.response.DailyEntrantCountResponse;
import com.lgcns.domain.entrance.repository.EntranceRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class EntranceServiceTest extends IntegrationTest {

    @Autowired private EntranceService entranceService;
    @Autowired private EntranceRepository entranceRepository;

    @Autowired private ManagerRepository managerRepository;
    @Autowired private PopupRepository popupRepository;

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
    class 입장자_수를_조회할_때 {

        @Test
        void 입장자가_없어도_조회에_성공한다() {
            // given
            Long popupId = popup.getId();

            // when
            DailyEntrantCountResponse response = entranceService.findDailyEntrantCount(popupId);

            // then
            assertThat(response.entrantCount()).isEqualTo(0);
        }

        @Test
        void 입장자가_있으면_조회에_성공한다() {
            // given
            Long popupId = popup.getId();
            entranceRepository.save(
                    Entrance.createPopupEnter(
                            popupId,
                            MemberGender.MALE,
                            MemberAge.TWENTIES,
                            LocalDate.now(),
                            LocalTime.parse("10:00:00")));
            entranceRepository.save(
                    Entrance.createPopupEnter(
                            popupId,
                            MemberGender.MALE,
                            MemberAge.TWENTIES,
                            LocalDate.now(),
                            LocalTime.parse("10:00:00")));
            entranceRepository.save(
                    Entrance.createPopupEnter(
                            popupId,
                            MemberGender.MALE,
                            MemberAge.TWENTIES,
                            LocalDate.now(),
                            LocalTime.parse("10:00:00")));
            entranceRepository.save(
                    Entrance.createPopupEnter(
                            popupId,
                            MemberGender.MALE,
                            MemberAge.TWENTIES,
                            LocalDate.now(),
                            LocalTime.parse("10:00:00")));
            entranceRepository.save(
                    Entrance.createPopupEnter(
                            popupId,
                            MemberGender.MALE,
                            MemberAge.TWENTIES,
                            LocalDate.now(),
                            LocalTime.parse("10:00:00")));

            // when
            DailyEntrantCountResponse response = entranceService.findDailyEntrantCount(popupId);

            // then
            assertThat(response.entrantCount()).isEqualTo(5);
        }

        @Test
        void 팝업_소유자가_아니면_입장자_수_조회에_실패한다() {
            // given
            Long popupId = popup.getId();
            setAuthentication(otherManager);

            // when, then
            assertThatThrownBy(() -> entranceService.findDailyEntrantCount(popupId))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(PopupErrorCode.POPUP_UNAUTHORIZED.getMessage());
        }

        @Test
        void 존재하지_않는_팝업에_대해_요청할_경우_조회에_실패한다() {
            // given
            Long popupId = -1L;

            // when, then
            assertThatThrownBy(() -> entranceService.findDailyEntrantCount(popupId))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(PopupErrorCode.POPUP_NOT_FOUND.getMessage());
        }
    }
}
