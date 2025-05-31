package com.lgcns.domain.visitorStats.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.entrance.domain.Entrance;
import com.lgcns.domain.entrance.domain.MemberAge;
import com.lgcns.domain.entrance.domain.MemberGender;
import com.lgcns.domain.entrance.repository.EntranceRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.visitorStats.domain.VisitorStats;
import com.lgcns.domain.visitorStats.dto.response.CountAndRatioResponse;
import com.lgcns.domain.visitorStats.dto.response.VisitorAnalysisResponse;
import com.lgcns.domain.visitorStats.exception.VisitorStatsErrorCode;
import com.lgcns.domain.visitorStats.repository.VisitorStatsRepository;
import com.lgcns.global.error.exception.CustomException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class VisitorStatsServiceTest extends IntegrationTest {

    @Autowired private VisitorStatsService visitorStatsService;

    @Autowired private VisitorStatsRepository visitorStatsRepository;

    @Autowired private PopupRepository popupRepository;

    @Autowired private ManagerRepository managerRepository;

    @Autowired private EntranceRepository entranceRepository;

    private Manager ownerManager;
    private Manager otherManager;

    @Nested
    class 방문자_분석을_조회할_때 {

        private Popup popup1;
        private Popup popup2;

        @BeforeEach
        void setUp() {
            ownerManager =
                    managerRepository.save(Manager.createManager("ownerManager", "ownerPassword"));
            otherManager =
                    managerRepository.save(Manager.createManager("otherManager", "otherPassword"));

            setAuthentication(ownerManager);

            popup1 =
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
            popup1 = popupRepository.save(popup1);

            popup2 =
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
            popup2 = popupRepository.save(popup2);

            List<VisitorStats> stats =
                    List.of(
                            VisitorStats.createVisitorStats(
                                    popup1.getId(),
                                    14,
                                    13,
                                    9,
                                    13,
                                    4,
                                    1,
                                    LocalDate.of(2025, 5, 14),
                                    LocalTime.of(8, 0)),
                            VisitorStats.createVisitorStats(
                                    popup1.getId(),
                                    13,
                                    14,
                                    8,
                                    14,
                                    4,
                                    1,
                                    LocalDate.of(2025, 5, 14),
                                    LocalTime.of(10, 0)),
                            VisitorStats.createVisitorStats(
                                    popup1.getId(),
                                    13,
                                    12,
                                    9,
                                    12,
                                    3,
                                    1,
                                    LocalDate.of(2025, 5, 14),
                                    LocalTime.of(12, 0)),
                            VisitorStats.createVisitorStats(
                                    popup1.getId(),
                                    15,
                                    12,
                                    8,
                                    14,
                                    3,
                                    2,
                                    LocalDate.of(2025, 5, 14),
                                    LocalTime.of(14, 0)),
                            VisitorStats.createVisitorStats(
                                    popup1.getId(),
                                    14,
                                    14,
                                    9,
                                    13,
                                    4,
                                    2,
                                    LocalDate.of(2025, 5, 14),
                                    LocalTime.of(16, 0)),
                            VisitorStats.createVisitorStats(
                                    popup1.getId(),
                                    13,
                                    12,
                                    8,
                                    13,
                                    3,
                                    1,
                                    LocalDate.of(2025, 5, 14),
                                    LocalTime.of(18, 0)),
                            VisitorStats.createVisitorStats(
                                    popup1.getId(),
                                    15,
                                    12,
                                    8,
                                    14,
                                    4,
                                    1,
                                    LocalDate.of(2025, 5, 14),
                                    LocalTime.of(20, 0)),
                            VisitorStats.createVisitorStats(
                                    popup1.getId(),
                                    13,
                                    13,
                                    7,
                                    15,
                                    3,
                                    1,
                                    LocalDate.of(2025, 5, 14),
                                    LocalTime.of(22, 0)),
                            VisitorStats.createVisitorStats(
                                    popup1.getId(),
                                    12,
                                    13,
                                    9,
                                    12,
                                    3,
                                    1,
                                    LocalDate.of(2025, 5, 15),
                                    LocalTime.of(8, 0)),
                            VisitorStats.createVisitorStats(
                                    popup1.getId(),
                                    14,
                                    13,
                                    8,
                                    13,
                                    4,
                                    2,
                                    LocalDate.of(2025, 5, 15),
                                    LocalTime.of(10, 0)),
                            VisitorStats.createVisitorStats(
                                    popup1.getId(),
                                    13,
                                    12,
                                    9,
                                    13,
                                    2,
                                    1,
                                    LocalDate.of(2025, 5, 15),
                                    LocalTime.of(12, 0)),
                            VisitorStats.createVisitorStats(
                                    popup1.getId(),
                                    14,
                                    14,
                                    8,
                                    14,
                                    4,
                                    2,
                                    LocalDate.of(2025, 5, 15),
                                    LocalTime.of(14, 0)),
                            VisitorStats.createVisitorStats(
                                    popup1.getId(),
                                    13,
                                    13,
                                    9,
                                    13,
                                    3,
                                    1,
                                    LocalDate.of(2025, 5, 15),
                                    LocalTime.of(16, 0)),
                            VisitorStats.createVisitorStats(
                                    popup1.getId(),
                                    12,
                                    13,
                                    9,
                                    12,
                                    3,
                                    1,
                                    LocalDate.of(2025, 5, 15),
                                    LocalTime.of(18, 0)),
                            VisitorStats.createVisitorStats(
                                    popup1.getId(),
                                    14,
                                    12,
                                    8,
                                    13,
                                    4,
                                    1,
                                    LocalDate.of(2025, 5, 15),
                                    LocalTime.of(20, 0)),
                            VisitorStats.createVisitorStats(
                                    popup1.getId(),
                                    13,
                                    13,
                                    7,
                                    14,
                                    4,
                                    1,
                                    LocalDate.of(2025, 5, 15),
                                    LocalTime.of(22, 0)));

            visitorStatsRepository.saveAll(stats);
        }

        @Test
        @Transactional
        void 방문자가_있으면_팝업_ID_방문자_통계_조회에_성공한다() {
            // given
            Long popup1Id = popup1.getId();

            // when
            VisitorAnalysisResponse analysisResponse =
                    visitorStatsService.getVisitorAnalysis(popup1Id);
            List<CountAndRatioResponse> gender = analysisResponse.gender();
            List<CountAndRatioResponse> age = analysisResponse.age();

            // then
            assertAll(
                    () -> {
                        assertThat(gender).hasSize(2);
                        assertThat(gender)
                                .anySatisfy(
                                        g -> {
                                            assertThat(g.name()).isEqualTo("남성");
                                            assertThat(g.count()).isEqualTo(215);
                                            assertThat(g.ratio()).isEqualTo(51);
                                        });
                        assertThat(gender)
                                .anySatisfy(
                                        g -> {
                                            assertThat(g.name()).isEqualTo("여성");
                                            assertThat(g.count()).isEqualTo(205);
                                            assertThat(g.ratio()).isEqualTo(49);
                                        });
                    },
                    () -> {
                        assertThat(age).hasSize(4);
                        assertThat(age)
                                .anySatisfy(
                                        a -> {
                                            assertThat(a.name()).isEqualTo("10대");
                                            assertThat(a.count()).isEqualTo(133);
                                            assertThat(a.ratio()).isEqualTo(32);
                                        });
                        assertThat(age)
                                .anySatisfy(
                                        a -> {
                                            assertThat(a.name()).isEqualTo("20대");
                                            assertThat(a.count()).isEqualTo(212);
                                            assertThat(a.ratio()).isEqualTo(50);
                                        });
                        assertThat(age)
                                .anySatisfy(
                                        a -> {
                                            assertThat(a.name()).isEqualTo("30대");
                                            assertThat(a.count()).isEqualTo(55);
                                            assertThat(a.ratio()).isEqualTo(13);
                                        });
                        assertThat(age)
                                .anySatisfy(
                                        a -> {
                                            assertThat(a.name()).isEqualTo("40대");
                                            assertThat(a.count()).isEqualTo(20);
                                            assertThat(a.ratio()).isEqualTo(5);
                                        });
                    });
        }

        @Test
        @Transactional
        void 방문자가_없으면_팝업_ID_방문자_통계_조회에_성공한다() {
            // given
            Long popup2Id = popup2.getId();

            // when
            VisitorAnalysisResponse analysisResponse =
                    visitorStatsService.getVisitorAnalysis(popup2Id);
            List<CountAndRatioResponse> gender = analysisResponse.gender();
            List<CountAndRatioResponse> age = analysisResponse.age();

            // then
            assertAll(() -> assertThat(gender).hasSize(0), () -> assertThat(age).hasSize(0));
        }

        @Test
        @Transactional
        void 존재하지_않는_팝업_방문자_분석_조회는_실패한다() {
            // given
            Long popupId = 3L;

            // when & then
            assertThrows(
                    CustomException.class,
                    () -> visitorStatsService.getVisitorAnalysis(popupId),
                    PopupErrorCode.POPUP_NOT_FOUND.getMessage());
        }

        @Test
        @Transactional
        void 팝업_소유자_아닌경우_방문자_분석_조회는_실패한다() {
            // given
            Long popupId = popup1.getId();
            setAuthentication(otherManager);

            // when & then
            assertThrows(
                    CustomException.class,
                    () -> visitorStatsService.getVisitorAnalysis(popupId),
                    PopupErrorCode.POPUP_UNAUTHORIZED.getMessage());
        }
    }

    @Nested
    class 진행_중인_팝업_id_리스트를_조회할_때 {

        @Test
        void 운영_중이고_입장_내역이_존재하며_중복된_방문자_분석이_없는_팝업이_존재하면_조회에_성공한다() {
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
            List<Long> result = visitorStatsService.findTargetPopupIds();

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
            List<Long> result = visitorStatsService.findTargetPopupIds();

            // then
            assertAll(() -> assertThat(result).isNotNull(), () -> assertThat(result).hasSize(0));
        }

        @Test
        void 운영_중인_팝업에_대해_입장_내역이_없으면_조회에_포함되지_않는다() {
            // given
            Popup popup = createRunningPopup();
            popupRepository.save(popup);

            // when
            List<Long> result = visitorStatsService.findTargetPopupIds();

            // then
            assertAll(() -> assertThat(result).isNotNull(), () -> assertThat(result).hasSize(0));
        }

        @Test
        void 운영_중인_팝업에_대해_중복된_방문자_분석이_존재하면_조회에_포함되지_않는다() {
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

            visitorStatsRepository.save(
                    VisitorStats.createVisitorStats(
                            popupId, 14, 13, 9, 13, 4, 1, nowDate, nowTime));

            // when
            List<Long> result = visitorStatsService.findTargetPopupIds();

            // then
            assertAll(() -> assertThat(result).isNotNull(), () -> assertThat(result).hasSize(0));
        }
    }

    @Nested
    class 방문자_분석을_생성할_때 {

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
            VisitorStats result = visitorStatsService.convertVisitorStats(popupId);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getPopupId()).isEqualTo(popupId),
                    () -> assertThat(result.getMaleCount()).isEqualTo(3),
                    () -> assertThat(result.getFemaleCount()).isEqualTo(2),
                    () -> assertThat(result.getTeenCount()).isEqualTo(1),
                    () -> assertThat(result.getTwentyCount()).isEqualTo(2),
                    () -> assertThat(result.getThirtyCount()).isEqualTo(1),
                    () -> assertThat(result.getFortyCount()).isEqualTo(1),
                    () -> assertThat(result.getAnalyzedDate()).isEqualTo(nowDate),
                    () ->
                            assertThat(result.getAnalyzedTime().truncatedTo(ChronoUnit.SECONDS))
                                    .isEqualTo(nowTime.truncatedTo(ChronoUnit.SECONDS)));
        }

        @Test
        void 한_시간_전_입장_내역이_없으면_예외가_발생한다() {
            // given
            Popup popup = createRunningPopup();
            popupRepository.save(popup);
            Long popupId = popup.getId();

            // when & then
            assertThrows(
                    CustomException.class,
                    () -> visitorStatsService.convertVisitorStats(popupId),
                    VisitorStatsErrorCode.HOURLY_ENTRANCE_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class 방문자_분석_리스트를_저장할_때 {

        @Test
        void 방문자_분석_리스트가_존재하면_저장에_성공한다() {
            // given
            Popup popup = createRunningPopup();
            popupRepository.save(popup);
            Long popupId = popup.getId();

            List<VisitorStats> visitorStatsList = createVisitorStatsList(popupId);

            // when
            visitorStatsService.createVisitorStats(visitorStatsList);

            // then
            List<VisitorStats> result = visitorStatsRepository.findAll();

            assertAll(
                    () -> assertThat(result).isNotEmpty(),
                    () -> assertThat(result).hasSize(3),
                    () ->
                            assertThat(result.get(0).getPopupId())
                                    .isEqualTo(visitorStatsList.get(0).getPopupId()),
                    () ->
                            assertThat(result.get(0).getMaleCount())
                                    .isEqualTo(visitorStatsList.get(0).getMaleCount()),
                    () ->
                            assertThat(result.get(0).getFemaleCount())
                                    .isEqualTo(visitorStatsList.get(0).getFemaleCount()),
                    () ->
                            assertThat(result.get(0).getTeenCount())
                                    .isEqualTo(visitorStatsList.get(0).getTeenCount()),
                    () ->
                            assertThat(result.get(0).getTwentyCount())
                                    .isEqualTo(visitorStatsList.get(0).getTwentyCount()),
                    () ->
                            assertThat(result.get(0).getThirtyCount())
                                    .isEqualTo(visitorStatsList.get(0).getThirtyCount()),
                    () ->
                            assertThat(result.get(0).getFortyCount())
                                    .isEqualTo(visitorStatsList.get(0).getFortyCount()),
                    () ->
                            assertThat(result.get(0).getAnalyzedDate())
                                    .isEqualTo(visitorStatsList.get(0).getAnalyzedDate()),
                    () ->
                            assertThat(result.get(0).getAnalyzedTime())
                                    .isEqualTo(visitorStatsList.get(0).getAnalyzedTime()));
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

    private List<VisitorStats> createVisitorStatsList(Long popupId) {
        List<VisitorStats> visitorStatsList = new ArrayList<>();

        VisitorStats visitorStats1 =
                VisitorStats.createVisitorStats(
                        popupId,
                        14,
                        13,
                        9,
                        13,
                        4,
                        1,
                        LocalDate.of(2025, 5, 31),
                        LocalTime.of(10, 0));
        visitorStatsList.add(visitorStats1);

        VisitorStats visitorStats2 =
                VisitorStats.createVisitorStats(
                        popupId,
                        14,
                        13,
                        9,
                        13,
                        4,
                        1,
                        LocalDate.of(2025, 5, 31),
                        LocalTime.of(11, 0));
        visitorStatsList.add(visitorStats2);

        VisitorStats visitorStats3 =
                VisitorStats.createVisitorStats(
                        popupId,
                        14,
                        13,
                        9,
                        13,
                        4,
                        1,
                        LocalDate.of(2025, 5, 31),
                        LocalTime.of(12, 0));
        visitorStatsList.add(visitorStats3);

        return visitorStatsList;
    }
}
