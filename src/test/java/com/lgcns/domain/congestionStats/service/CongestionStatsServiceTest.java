package com.lgcns.domain.congestionStats.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.congestionStats.domain.CongestionStats;
import com.lgcns.domain.congestionStats.dto.response.CongestionStatsResponse;
import com.lgcns.domain.congestionStats.dto.response.DailyCongestionStatsResponse;
import com.lgcns.domain.congestionStats.repository.CongestionStatsRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.reservationStats.dto.response.DayOfWeek;
import com.lgcns.global.error.exception.CustomException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CongestionStatsServiceTest extends IntegrationTest {
    @Autowired CongestionStatsService congestionStatsService;
    @Autowired CongestionStatsRepository congestionStatsRepository;

    @Autowired ManagerRepository managerRepository;
    @Autowired PopupRepository popupRepository;

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
    class 혼잡도_분석_조회 {
        @Test
        @Transactional
        void 평균값으로_계산된_혼잡도_분석_조회에_성공한다() {
            // given
            final Long popupId = popup.getId();
            createCongestionStats(popup);

            // when
            CongestionStatsResponse response = congestionStatsService.getCongestionStats(popupId);

            // then
            assertAll(
                    () -> assertThat(response.mon()).hasSize(6),
                    () -> assertThat(response.tue()).hasSize(6),
                    () -> assertThat(response.wed()).hasSize(6),
                    () -> assertThat(response.thu()).hasSize(6),
                    () -> assertThat(response.fri()).hasSize(6),
                    () -> assertThat(response.sat()).hasSize(6),
                    () -> assertThat(response.sun()).hasSize(6),
                    () ->
                            assertThat(response.sat())
                                    .allSatisfy(r -> assertThat(r.value()).isEqualTo(70)));
        }

        @Test
        @Transactional
        void 혼잡도_데이터가_없으면_0을_반환한다() {
            // given
            final Long popupId = popup.getId();
            createCongestionStats(popup); // 월요일 22시만 누락된 상태로 10일치 생성

            // when
            CongestionStatsResponse response = congestionStatsService.getCongestionStats(popupId);

            // then
            List<DailyCongestionStatsResponse> mondayData = response.mon();

            Optional<DailyCongestionStatsResponse> hour22 =
                    mondayData.stream().filter(d -> d.time() != null && d.time() == 20).findFirst();

            assertThat(hour22).isPresent();
            assertThat(hour22.get().value()).isZero();
        }

        @Test
        @Transactional
        void 권한이_없는_사용자가_혼잡도_분석을_조회하면_예외가_발생한다() {
            // given
            setAuthentication(otherManager);
            final Long popupId = popup.getId();

            // when & then
            assertThatThrownBy(() -> congestionStatsService.getCongestionStats(popupId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_UNAUTHORIZED);
        }

        @Test
        @Transactional
        void 존재하지_않는_팝업에_대해_혼잡도_분석을_조회하면_예외가_발생한다() {
            // given
            final Long popupId = 9999L;

            // when & then
            assertThatThrownBy(() -> congestionStatsService.getCongestionStats(popupId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_NOT_FOUND);
        }
    }

    private void createCongestionStats(Popup popup) {
        Long popupId = popup.getId();
        LocalDate startDate = LocalDate.of(2025, 5, 17);
        int days = 10;

        for (int i = 0; i < days; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(currentDate.getDayOfWeek().name());

            for (int hour = 10; hour <= 20; hour += 2) {
                // 월요일 22시만 제외
                if (dayOfWeek == DayOfWeek.MONDAY && hour == 20) continue;

                LocalTime analyzedTime = LocalTime.of(hour, 0);
                int entrantCount = (i < 7) ? 60 : 80; // 1주차는 60, 2주차는 80

                CongestionStats stats =
                        CongestionStats.createCongestionStats(
                                popupId, entrantCount, dayOfWeek, currentDate, analyzedTime);

                congestionStatsRepository.save(stats);
            }
        }
    }
}
