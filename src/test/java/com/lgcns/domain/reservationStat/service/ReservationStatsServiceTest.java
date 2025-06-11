package com.lgcns.domain.reservationStat.service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.lgcns.IntegrationTest;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.reservationStats.domain.DayOfWeekReservationCount;
import com.lgcns.domain.reservationStats.dto.response.DayOfWeek;
import com.lgcns.domain.reservationStats.dto.response.DayOfWeekReservationCountResponse;
import com.lgcns.domain.reservationStats.dto.response.ReservationStatsResponse;
import com.lgcns.domain.reservationStats.exception.ReservationStatsErrorCode;
import com.lgcns.domain.reservationStats.repository.DayOfWeekReservationCountRepository;
import com.lgcns.domain.reservationStats.scheduler.DayOfWeekReservationStatsScheduler;
import com.lgcns.domain.reservationStats.service.ReservationStatsService;
import com.lgcns.global.error.exception.CustomException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public class ReservationStatsServiceTest extends IntegrationTest {

    @Autowired private ReservationStatsService reservationStatsService;
    @Autowired private ManagerRepository managerRepository;
    @Autowired private PopupRepository popupRepository;
    @Autowired private DayOfWeekReservationCountRepository dayOfWeekReservationCountRepository;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private DayOfWeekReservationStatsScheduler scheduler;

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
    class 예약_통계를_조회할_때 {

        @Test
        void 예약_통계_조회에_성공한다() throws JsonProcessingException {
            // given
            Long popupId = popup.getId();

            String expectedResponse =
                    objectMapper.writeValueAsString(Map.of("reservationCount", 10));
            stubFindDailyMemberReservationCount(popupId, 200, expectedResponse);

            dayOfWeekReservationCountRepository.save(
                    DayOfWeekReservationCount.createDayOfWeekReservationCount(
                            popupId, 5, 8, 12, 15, 20, 25, 18));

            // when
            ReservationStatsResponse result = reservationStatsService.getReservationStats(popupId);

            // then
            Assertions.assertAll(
                    () -> assertThat(result.reservedCount()).isEqualTo(10),
                    () -> assertThat(result.chart()).hasSize(7),
                    () ->
                            assertThat(result.chart().get(0))
                                    .isEqualTo(
                                            DayOfWeekReservationCountResponse.of(
                                                    DayOfWeek.MONDAY, 5)),
                    () ->
                            assertThat(result.chart().get(1))
                                    .isEqualTo(
                                            DayOfWeekReservationCountResponse.of(
                                                    DayOfWeek.TUESDAY, 8)),
                    () ->
                            assertThat(result.chart().get(2))
                                    .isEqualTo(
                                            DayOfWeekReservationCountResponse.of(
                                                    DayOfWeek.WEDNESDAY, 12)),
                    () ->
                            assertThat(result.chart().get(3))
                                    .isEqualTo(
                                            DayOfWeekReservationCountResponse.of(
                                                    DayOfWeek.THURSDAY, 15)),
                    () ->
                            assertThat(result.chart().get(4))
                                    .isEqualTo(
                                            DayOfWeekReservationCountResponse.of(
                                                    DayOfWeek.FRIDAY, 20)),
                    () ->
                            assertThat(result.chart().get(5))
                                    .isEqualTo(
                                            DayOfWeekReservationCountResponse.of(
                                                    DayOfWeek.SATURDAY, 25)),
                    () ->
                            assertThat(result.chart().get(6))
                                    .isEqualTo(
                                            DayOfWeekReservationCountResponse.of(
                                                    DayOfWeek.SUNDAY, 18)));
        }

        @Test
        void 요일별_예약_통계가_없는_경우_모든_요일이_0으로_반환된다() throws JsonProcessingException {
            // given
            Long popupId = popup.getId();

            String expectedResponse =
                    objectMapper.writeValueAsString(Map.of("reservationCount", 5));
            stubFindDailyMemberReservationCount(popupId, 200, expectedResponse);

            // when
            ReservationStatsResponse result = reservationStatsService.getReservationStats(popupId);

            // then
            Assertions.assertAll(
                    () -> assertThat(result.reservedCount()).isEqualTo(5),
                    () -> assertThat(result.chart()).hasSize(7),
                    () ->
                            assertThat(result.chart().get(0))
                                    .isEqualTo(
                                            DayOfWeekReservationCountResponse.of(
                                                    DayOfWeek.MONDAY, 0)),
                    () ->
                            assertThat(result.chart().get(1))
                                    .isEqualTo(
                                            DayOfWeekReservationCountResponse.of(
                                                    DayOfWeek.TUESDAY, 0)),
                    () ->
                            assertThat(result.chart().get(2))
                                    .isEqualTo(
                                            DayOfWeekReservationCountResponse.of(
                                                    DayOfWeek.WEDNESDAY, 0)),
                    () ->
                            assertThat(result.chart().get(3))
                                    .isEqualTo(
                                            DayOfWeekReservationCountResponse.of(
                                                    DayOfWeek.THURSDAY, 0)),
                    () ->
                            assertThat(result.chart().get(4))
                                    .isEqualTo(
                                            DayOfWeekReservationCountResponse.of(
                                                    DayOfWeek.FRIDAY, 0)),
                    () ->
                            assertThat(result.chart().get(5))
                                    .isEqualTo(
                                            DayOfWeekReservationCountResponse.of(
                                                    DayOfWeek.SATURDAY, 0)),
                    () ->
                            assertThat(result.chart().get(6))
                                    .isEqualTo(
                                            DayOfWeekReservationCountResponse.of(
                                                    DayOfWeek.SUNDAY, 0)));
        }

        @Test
        void 다른_관리자가_소유한_팝업에_대한_예약_통계_조회는_실패한다() {
            // given
            Long popupId = popup.getId();
            setAuthentication(otherManager);

            // when & then
            CustomException exception =
                    assertThrows(
                            CustomException.class,
                            () -> reservationStatsService.getReservationStats(popupId));

            assertThat(exception.getErrorCode()).isEqualTo(PopupErrorCode.POPUP_UNAUTHORIZED);
        }

        @Test
        void 존재하지_않는_팝업에_대한_예약_통계_조회는_실패한다() {
            // given
            Long invalidPopupId = 99999L;

            // when & then
            CustomException exception =
                    assertThrows(
                            CustomException.class,
                            () -> reservationStatsService.getReservationStats(invalidPopupId));

            assertThat(exception.getErrorCode()).isEqualTo(PopupErrorCode.POPUP_NOT_FOUND);
        }
    }

    @Nested
    class 요일별_통계_테이블을_업데이트할_때 {

        @Test
        void 모든_팝업의_요일별_예약_통계_업데이트에_성공한다() throws JsonProcessingException {
            // given
            // 테스트를 위해 추가 팝업 생성
            Popup popup2 =
                    Popup.createPopup(
                            ownerManager,
                            "testPopup2",
                            "https://bucket/이미지2.jpg",
                            LocalDate.parse("2025-02-01"),
                            LocalDate.parse("2025-02-28"),
                            LocalDateTime.parse("2025-02-01T10:00:00"),
                            LocalDateTime.parse("2025-02-28T20:00:00"),
                            LocalTime.parse("10:00:00"),
                            LocalTime.parse("20:00:00"),
                            100,
                            20,
                            "서울특별시 강남구 테헤란로 456",
                            "4층 B호",
                            37.654321,
                            127.654321);
            popup2 = popupRepository.save(popup2);

            Map<String, Object> statsData =
                    Map.of(
                            popup.getId().toString(),
                                    Map.of(
                                            "popupId", popup.getId(),
                                            "mondayCount", 10,
                                            "tuesdayCount", 12,
                                            "wednesdayCount", 8,
                                            "thursdayCount", 15,
                                            "fridayCount", 20,
                                            "saturdayCount", 25,
                                            "sundayCount", 18),
                            popup2.getId().toString(),
                                    Map.of(
                                            "popupId", popup2.getId(),
                                            "mondayCount", 5,
                                            "tuesdayCount", 7,
                                            "wednesdayCount", 3,
                                            "thursdayCount", 9,
                                            "fridayCount", 11,
                                            "saturdayCount", 13,
                                            "sundayCount", 6));

            String expectedResponse = objectMapper.writeValueAsString(statsData);
            stubGetAllDayOfWeekReservationStats(200, expectedResponse);

            // when
            reservationStatsService.updateAllDayOfWeekReservationStats();

            // then
            DayOfWeekReservationCount popup1Stats =
                    dayOfWeekReservationCountRepository.findByPopupId(popup.getId()).orElseThrow();
            DayOfWeekReservationCount popup2Stats =
                    dayOfWeekReservationCountRepository.findByPopupId(popup2.getId()).orElseThrow();

            Assertions.assertAll(
                    () -> assertThat(popup1Stats.getMondayCount()).isEqualTo(10),
                    () -> assertThat(popup1Stats.getTuesdayCount()).isEqualTo(12),
                    () -> assertThat(popup1Stats.getWednesdayCount()).isEqualTo(8),
                    () -> assertThat(popup1Stats.getThursdayCount()).isEqualTo(15),
                    () -> assertThat(popup1Stats.getFridayCount()).isEqualTo(20),
                    () -> assertThat(popup1Stats.getSaturdayCount()).isEqualTo(25),
                    () -> assertThat(popup1Stats.getSundayCount()).isEqualTo(18),
                    () -> assertThat(popup2Stats.getMondayCount()).isEqualTo(5),
                    () -> assertThat(popup2Stats.getTuesdayCount()).isEqualTo(7),
                    () -> assertThat(popup2Stats.getWednesdayCount()).isEqualTo(3),
                    () -> assertThat(popup2Stats.getThursdayCount()).isEqualTo(9),
                    () -> assertThat(popup2Stats.getFridayCount()).isEqualTo(11),
                    () -> assertThat(popup2Stats.getSaturdayCount()).isEqualTo(13),
                    () -> assertThat(popup2Stats.getSundayCount()).isEqualTo(6));
        }

        @Test
        void 기존_데이터가_있는_경우_업데이트에_성공한다() throws JsonProcessingException {
            // given
            dayOfWeekReservationCountRepository.save(
                    DayOfWeekReservationCount.createDayOfWeekReservationCount(
                            popup.getId(), 1, 2, 3, 4, 5, 6, 7));

            Map<String, Object> statsData =
                    Map.of(
                            popup.getId().toString(),
                            Map.of(
                                    "popupId", popup.getId(),
                                    "mondayCount", 15,
                                    "tuesdayCount", 18,
                                    "wednesdayCount", 22,
                                    "thursdayCount", 25,
                                    "fridayCount", 30,
                                    "saturdayCount", 35,
                                    "sundayCount", 28));

            String expectedResponse = objectMapper.writeValueAsString(statsData);
            stubGetAllDayOfWeekReservationStats(200, expectedResponse);

            // when
            reservationStatsService.updateAllDayOfWeekReservationStats();

            // then
            DayOfWeekReservationCount updatedStats =
                    dayOfWeekReservationCountRepository.findByPopupId(popup.getId()).orElseThrow();

            Assertions.assertAll(
                    () -> assertThat(updatedStats.getMondayCount()).isEqualTo(15),
                    () -> assertThat(updatedStats.getTuesdayCount()).isEqualTo(18),
                    () -> assertThat(updatedStats.getWednesdayCount()).isEqualTo(22),
                    () -> assertThat(updatedStats.getThursdayCount()).isEqualTo(25),
                    () -> assertThat(updatedStats.getFridayCount()).isEqualTo(30),
                    () -> assertThat(updatedStats.getSaturdayCount()).isEqualTo(35),
                    () -> assertThat(updatedStats.getSundayCount()).isEqualTo(28));
        }

        @Test
        void 예약_서비스에서_데이터가_없는_팝업은_업데이트되지_않는다() throws JsonProcessingException {
            // given
            // 테스트를 위해 추가 팝업 생성
            Popup popup2 =
                    Popup.createPopup(
                            ownerManager,
                            "testPopup2",
                            "https://bucket/이미지2.jpg",
                            LocalDate.parse("2025-02-01"),
                            LocalDate.parse("2025-02-28"),
                            LocalDateTime.parse("2025-02-01T10:00:00"),
                            LocalDateTime.parse("2025-02-28T20:00:00"),
                            LocalTime.parse("10:00:00"),
                            LocalTime.parse("20:00:00"),
                            100,
                            20,
                            "서울특별시 강남구 테헤란로 456",
                            "4층 B호",
                            37.654321,
                            127.654321);
            popup2 = popupRepository.save(popup2);

            Map<String, Object> statsData =
                    Map.of(
                            popup2.getId().toString(),
                            Map.of(
                                    "popupId", popup2.getId(),
                                    "mondayCount", 5,
                                    "tuesdayCount", 7,
                                    "wednesdayCount", 3,
                                    "thursdayCount", 9,
                                    "fridayCount", 11,
                                    "saturdayCount", 13,
                                    "sundayCount", 6));

            String expectedResponse = objectMapper.writeValueAsString(statsData);
            stubGetAllDayOfWeekReservationStats(200, expectedResponse);

            // when
            reservationStatsService.updateAllDayOfWeekReservationStats();

            // then
            assertThat(dayOfWeekReservationCountRepository.findByPopupId(popup.getId())).isEmpty();
            assertThat(dayOfWeekReservationCountRepository.findByPopupId(popup2.getId()))
                    .isPresent();
        }

        @Test
        void 예약_서비스_연결_실패시_예외가_발생한다() {
            // given
            wireMockServer.stop();

            // when & then
            CustomException exception =
                    assertThrows(
                            CustomException.class,
                            () -> reservationStatsService.updateAllDayOfWeekReservationStats());

            assertThat(exception.getErrorCode())
                    .isEqualTo(ReservationStatsErrorCode.RESERVATION_SERVICE_CONNECTION_FAILED);

            wireMockServer.start();
        }

        @Test
        void 예약_서비스_오류시_예외가_발생한다() {
            // given
            wireMockServer.stubFor(
                    get(urlPathEqualTo("/internal/day-of-week-count"))
                            .willReturn(aResponse().withStatus(500)));

            // when & then
            CustomException exception =
                    assertThrows(
                            CustomException.class,
                            () -> reservationStatsService.updateAllDayOfWeekReservationStats());

            assertThat(exception.getErrorCode())
                    .isEqualTo(ReservationStatsErrorCode.RESERVATION_SERVICE_ERROR);
        }
    }

    private void stubFindDailyMemberReservationCount(Long popupId, int status, String body) {
        MappingBuilder mappingBuilder =
                get(urlPathEqualTo("/internal/" + popupId + "/daily-count"));

        wireMockServer.stubFor(
                mappingBuilder.willReturn(
                        aResponse()
                                .withStatus(status)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(body)));
    }

    private void stubGetAllDayOfWeekReservationStats(int status, String body) {
        MappingBuilder mappingBuilder = get(urlPathEqualTo("/internal/day-of-week-count"));

        wireMockServer.stubFor(
                mappingBuilder.willReturn(
                        aResponse()
                                .withStatus(status)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(body)));
    }
}
