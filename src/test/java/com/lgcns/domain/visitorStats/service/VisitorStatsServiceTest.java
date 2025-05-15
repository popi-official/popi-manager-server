package com.lgcns.domain.visitorStats.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.visitorStats.domain.VisitorStats;
import com.lgcns.domain.visitorStats.dto.response.CountAndRatioResponse;
import com.lgcns.domain.visitorStats.dto.response.VisitorAnalysisResponse;
import com.lgcns.domain.visitorStats.repository.VisitorStatsRepository;
import com.lgcns.global.error.exception.CustomException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    private Manager ownerManager;
    private Manager otherManager;
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

    @Nested
    class 방문자_분석_조회 {

        @Test
        @Transactional
        void 방문자_있을때_팝업_ID_방문자_통계_조회에_성공한다() {
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
        void 방문자_없을때_팝업_ID_방문자_통계_조회에_성공한다() {
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
}
