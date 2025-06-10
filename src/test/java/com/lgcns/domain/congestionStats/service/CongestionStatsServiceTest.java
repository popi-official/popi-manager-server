package com.lgcns.domain.congestionStats.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.congestionStats.domain.CongestionStats;
import com.lgcns.domain.congestionStats.dto.response.CongestionStatsResponse;
import com.lgcns.domain.congestionStats.dto.response.DailyCongestionStatsResponse;
import com.lgcns.domain.congestionStats.repository.CongestionStatsRepository;
import com.lgcns.domain.entrance.domain.Entrance;
import com.lgcns.domain.entrance.domain.MemberAge;
import com.lgcns.domain.entrance.domain.MemberGender;
import com.lgcns.domain.entrance.repository.EntranceRepository;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    @Autowired EntranceRepository entranceRepository;

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

    @Nested
    class 진행_중인_팝업_id_리스트를_조회할_때 {

        @Test
        void 운영_중이고_입장_내역이_존재하며_중복된_혼잡도_분석이_없는_팝업이_존재하면_조회에_성공한다() {
            // given
            Popup popup = createRunningPopup();
            popupRepository.save(popup);
            Long popupId = popup.getId();

            entranceRepository.save(
                    Entrance.createPopupEnter(
                            popupId,
                            MemberGender.MALE,
                            MemberAge.TWENTIES,
                            LocalDate.now(),
                            LocalTime.parse("10:00:00")));

            // when
            List<Long> result = congestionStatsService.findTargetPopupIds();

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result).hasSize(1),
                    () -> assertThat(result.get(0)).isEqualTo(popupId));
        }

        @Test
        void 운영_중인_팝업이_없으면_빈_리스트가_조회된다() {
            // given
            Popup popup = createClosedPopup();
            popupRepository.save(popup);

            // when
            List<Long> result = congestionStatsService.findTargetPopupIds();

            // then
            assertAll(() -> assertThat(result).isNotNull(), () -> assertThat(result).hasSize(0));
        }

        @Test
        void 운영_중인_팝업에_대해_입장_내역이_없으면_조회에_포함되지_않는다() {
            // given
            Popup popup = createRunningPopup();
            popupRepository.save(popup);

            // when
            List<Long> result = congestionStatsService.findTargetPopupIds();

            // then
            assertAll(() -> assertThat(result).isNotNull(), () -> assertThat(result).hasSize(0));
        }

        @Test
        void 운영_중인_팝업에_대해_중복된_혼잡도_분석이_존재하면_조회에_포함되지_않는다() {
            // given
            Popup popup = createRunningPopup();
            popupRepository.save(popup);
            Long popupId = popup.getId();

            entranceRepository.save(
                    Entrance.createPopupEnter(
                            popupId,
                            MemberGender.MALE,
                            MemberAge.TWENTIES,
                            LocalDate.now(),
                            LocalTime.parse("10:00:00")));

            LocalDate nowDate = LocalDate.now();
            LocalTime nowTime = LocalTime.now();

            congestionStatsRepository.save(
                    CongestionStats.createCongestionStats(
                            popupId, 1, DayOfWeek.MONDAY, nowDate, nowTime));

            // when
            List<Long> result = congestionStatsService.findTargetPopupIds();

            // then
            assertAll(() -> assertThat(result).isNotNull(), () -> assertThat(result).hasSize(0));
        }
    }

    @Nested
    class 혼잡도_분석을_생성할_때 {

        @Test
        void 한_시간_전_입장_내역이_존재하면_생성에_성공한다() {
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
                            MemberAge.TEENAGER,
                            nowDate,
                            nowTime.minusHours(1).truncatedTo(ChronoUnit.HOURS)));
            entranceRepository.save(
                    Entrance.createPopupEnter(
                            popupId,
                            MemberGender.FEMALE,
                            MemberAge.TWENTIES,
                            nowDate,
                            nowTime.minusHours(1).truncatedTo(ChronoUnit.HOURS)));
            entranceRepository.save(
                    Entrance.createPopupEnter(
                            popupId,
                            MemberGender.MALE,
                            MemberAge.THIRTIES,
                            nowDate,
                            nowTime.minusHours(1).truncatedTo(ChronoUnit.HOURS)));
            entranceRepository.save(
                    Entrance.createPopupEnter(
                            popupId,
                            MemberGender.FEMALE,
                            MemberAge.FORTIES_AND_ABOVE,
                            nowDate,
                            nowTime.minusHours(1).truncatedTo(ChronoUnit.HOURS)));
            entranceRepository.save(
                    Entrance.createPopupEnter(
                            popupId,
                            MemberGender.MALE,
                            MemberAge.TWENTIES,
                            nowDate,
                            nowTime.minusHours(1).truncatedTo(ChronoUnit.HOURS)));

            // when
            CongestionStats result = congestionStatsService.convertCongestionStats(popupId);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getPopupId()).isEqualTo(popupId),
                    () -> assertThat(result.getEntrantCount()).isEqualTo(5),
                    () ->
                            assertThat(result.getDayOfWeek())
                                    .isEqualTo(DayOfWeek.valueOf(nowDate.getDayOfWeek().name())),
                    () -> assertThat(result.getAnalyzedDate()).isEqualTo(nowDate),
                    () ->
                            assertThat(result.getAnalyzedTime().truncatedTo(ChronoUnit.SECONDS))
                                    .isEqualTo(nowTime.truncatedTo(ChronoUnit.SECONDS)));
        }

        @Test
        void 한_시간_전_입장_내역이_없으면_입장자_카운트에_0이_저장된다() {
            // given
            Popup popup = createRunningPopup();
            popupRepository.save(popup);
            Long popupId = popup.getId();

            LocalDate nowDate = LocalDate.now();
            LocalTime nowTime = LocalTime.now();

            // when
            CongestionStats result = congestionStatsService.convertCongestionStats(popupId);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getPopupId()).isEqualTo(popupId),
                    () -> assertThat(result.getEntrantCount()).isEqualTo(0),
                    () ->
                            assertThat(result.getDayOfWeek())
                                    .isEqualTo(DayOfWeek.valueOf(nowDate.getDayOfWeek().name())),
                    () -> assertThat(result.getAnalyzedDate()).isEqualTo(nowDate),
                    () ->
                            assertThat(result.getAnalyzedTime().truncatedTo(ChronoUnit.SECONDS))
                                    .isEqualTo(nowTime.truncatedTo(ChronoUnit.SECONDS)));
        }
    }

    @Nested
    class 혼잡도_분석_리스트를_저장할_때 {

        @Test
        void 혼잡도_분석_리스트가_존재하면_저장에_성공한다() {
            // given
            Popup popup = createRunningPopup();
            popupRepository.save(popup);
            Long popupId = popup.getId();

            List<CongestionStats> congestionStatsList = createCongestionStatsList(popupId);

            // when
            congestionStatsService.createCongestionStats(congestionStatsList);

            // then
            List<CongestionStats> result = congestionStatsRepository.findAll();

            assertAll(
                    () -> assertThat(result).isNotEmpty(),
                    () -> assertThat(result).hasSize(3),
                    () ->
                            assertThat(result.get(0).getPopupId())
                                    .isEqualTo(congestionStatsList.get(0).getPopupId()),
                    () ->
                            assertThat(result.get(0).getEntrantCount())
                                    .isEqualTo(congestionStatsList.get(0).getEntrantCount()),
                    () ->
                            assertThat(result.get(0).getDayOfWeek())
                                    .isEqualTo(congestionStatsList.get(0).getDayOfWeek()),
                    () ->
                            assertThat(result.get(0).getAnalyzedDate())
                                    .isEqualTo(congestionStatsList.get(0).getAnalyzedDate()),
                    () ->
                            assertThat(result.get(0).getAnalyzedTime())
                                    .isEqualTo(congestionStatsList.get(0).getAnalyzedTime()));
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

    private List<CongestionStats> createCongestionStatsList(Long popupId) {
        List<CongestionStats> congestionStatsList = new ArrayList<>();

        LocalDate date = LocalDate.of(2025, 6, 10);
        java.time.DayOfWeek dayOfWeek = date.getDayOfWeek();

        CongestionStats stats1 =
                CongestionStats.createCongestionStats(
                        popupId,
                        20,
                        DayOfWeek.valueOf(dayOfWeek.name()),
                        date,
                        LocalTime.of(10, 0));

        CongestionStats stats2 =
                CongestionStats.createCongestionStats(
                        popupId,
                        35,
                        DayOfWeek.valueOf(dayOfWeek.name()),
                        date,
                        LocalTime.of(11, 0));

        CongestionStats stats3 =
                CongestionStats.createCongestionStats(
                        popupId,
                        42,
                        DayOfWeek.valueOf(dayOfWeek.name()),
                        date,
                        LocalTime.of(12, 0));

        congestionStatsList.add(stats1);
        congestionStatsList.add(stats2);
        congestionStatsList.add(stats3);

        return congestionStatsList;
    }
}
