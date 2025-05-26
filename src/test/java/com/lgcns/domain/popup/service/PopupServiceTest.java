package com.lgcns.domain.popup.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.dto.request.PopupCreateRequest;
import com.lgcns.domain.popup.dto.request.PopupIdsRequest;
import com.lgcns.domain.popup.dto.request.PopupWithChoicesCreateRequest;
import com.lgcns.domain.popup.dto.response.*;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.reservation.domain.Reservation;
import com.lgcns.domain.reservation.repository.ReservationRepository;
import com.lgcns.domain.survey.domain.Choice;
import com.lgcns.domain.survey.domain.Survey;
import com.lgcns.domain.survey.dto.request.ChoiceCreateRequest;
import com.lgcns.domain.survey.repository.ChoiceRepository;
import com.lgcns.domain.survey.repository.SurveyRepository;
import com.lgcns.global.common.response.SliceResponse;
import com.lgcns.global.error.exception.CustomException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class PopupServiceTest extends IntegrationTest {
    @Autowired private PopupService popupService;
    @Autowired private PopupRepository popupRepository;
    @Autowired private SurveyRepository surveyRepository;

    @Autowired private ManagerRepository managerRepository;
    @Autowired private ChoiceRepository choiceRepository;
    @Autowired private ReservationRepository reservationRepository;

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
    }

    @Nested
    class 팝업_등록 {
        @Test
        @Transactional
        void 유효한_입력_값이면_팝업_등록에_성공한다() {
            // given
            PopupWithChoicesCreateRequest popupWithChoicesCreateRequest =
                    createPopupWithChoicesCreateRequest();

            // when
            PopupCreateResponse response = popupService.createPopup(popupWithChoicesCreateRequest);

            // then
            Popup savedPopup = popupRepository.findById(response.popupId()).orElseThrow();
            List<Survey> surveyList = surveyRepository.findAll();
            List<Choice> choiceList = choiceRepository.findAll();
            List<Reservation> reservationList = reservationRepository.findAll();

            Assertions.assertAll(
                    () -> assertThat(response.popupId()).isEqualTo(savedPopup.getId()),
                    () -> assertThat(savedPopup.getName()).isEqualTo("popup1"),
                    () -> assertThat(savedPopup.getImageUrl()).isEqualTo("https://bucket/asdf"),
                    () ->
                            assertThat(savedPopup.getPopupStartDate())
                                    .isEqualTo(LocalDate.parse("2025-01-01")),
                    () ->
                            assertThat(savedPopup.getPopupEndDate())
                                    .isEqualTo(LocalDate.parse("2025-01-31")),
                    () -> assertThat(surveyList).hasSize(4),
                    () -> assertThat(choiceList).hasSize(16),
                    () -> assertThat(reservationList).hasSize(31 * 11));
        }
    }

    @Nested
    class 팝업_목록_조회 {
        @Test
        @Transactional
        void 팝업_목록_조회에_성공한다() {
            // given
            createPopup();
            createPopup();

            // when
            List<PopupPreviewResponse> popupPreviewResponseList = popupService.findAllPopups();

            // then
            Assertions.assertAll(
                    () -> assertThat(popupPreviewResponseList).hasSize(2), // 기존 팝업 + 새로 생성된 팝업
                    () -> assertThat(popupPreviewResponseList.get(0).popupId()).isNotNull(),
                    () -> assertThat(popupPreviewResponseList.get(1).popupId()).isNotNull());
        }
    }

    @Nested
    class 팝업_삭제 {
        @Test
        @Transactional
        void 정상적으로_팝업을_삭제한다() {
            // given
            Long popupId = createPopup();

            // when
            popupService.deletePopup(popupId);

            // then
            assertThat(popupRepository.findById(popupId)).isEmpty();
        }

        @Test
        @Transactional
        void 존재하지_않는_팝업을_삭제하면_예외가_발생한다() {
            // given
            final Long nonExistentPopupId = 9999L;

            // when & then
            assertThatThrownBy(() -> popupService.deletePopup(nonExistentPopupId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_NOT_FOUND);
        }

        @Test
        @Transactional
        void 권한이_없는_사용자가_팝업을_삭제하면_예외가_발생한다() {
            // given
            final Long popupId = createPopup();

            setAuthentication(otherManager);

            // when & then
            assertThatThrownBy(() -> popupService.deletePopup(popupId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_UNAUTHORIZED);
        }
    }

    @Nested
    class 내부용_팝업_목록을_조회할_때 {
        @Test
        @Transactional
        void 운영중인_팝업이_존재하면_리스트를_반환한다() {
            LocalDate now = LocalDate.now();

            Long activePopup1 = createPopupWithDates("운영중인팝업1", now.minusDays(5), now.plusDays(10));
            Long activePopup2 = createPopupWithDates("운영중인팝업2", now.minusDays(1), now.plusDays(5));
            Long activePopup3 = createPopupWithDates("운영중인팝업3", now, now.plusDays(15));

            createPopupWithDates("종료된팝업1", now.minusDays(20), now.minusDays(1));

            Long activePopup4 = createPopupWithDates("예정팝업1", now.plusDays(10), now.plusDays(20));

            // when
            SliceResponse<PopupInfoResponse> response =
                    popupService.findPopupsByNameWithPagination(null, null, 10);

            // then
            Assertions.assertAll(
                    () -> assertThat(response.content()).hasSize(4),
                    () ->
                            assertThat(response.content())
                                    .extracting("popupId")
                                    .containsExactlyInAnyOrder(
                                            activePopup1, activePopup2, activePopup3, activePopup4),
                    () -> assertThat(response.isLast()).isTrue());
        }

        @Test
        @Transactional
        void 운영중인_팝업이_없으면_빈_리스트를_반환한다() {
            // given
            LocalDate now = LocalDate.now();

            // 운영 종료된 팝업들만 생성
            createPopupWithDates("종료된팝업1", now.minusDays(30), now.minusDays(10));
            createPopupWithDates("종료된팝업2", now.minusDays(20), now.minusDays(5));

            // when
            SliceResponse<PopupInfoResponse> response =
                    popupService.findPopupsByNameWithPagination(null, null, 10);

            // then
            Assertions.assertAll(
                    () -> assertThat(response.content()).isEmpty(),
                    () -> assertThat(response.isLast()).isTrue());
        }

        @Test
        @Transactional
        void 페이징_처리가_정상적으로_동작한다() {
            // given
            LocalDate now = LocalDate.now();

            // 운영중인 팝업 5개 생성
            for (int i = 1; i <= 5; i++) {
                createPopupWithDates("운영중인팝업" + i, now.minusDays(1), now.plusDays(10));
            }

            // when - 첫 번째 페이지 (size=3)
            SliceResponse<PopupInfoResponse> firstPage =
                    popupService.findPopupsByNameWithPagination(null, null, 3);

            // then
            Assertions.assertAll(
                    () -> assertThat(firstPage.content()).hasSize(3),
                    () -> assertThat(firstPage.isLast()).isFalse());

            // when - 두 번째 페이지
            Long lastPopupId = firstPage.content().get(firstPage.content().size() - 1).popupId();
            SliceResponse<PopupInfoResponse> secondPage =
                    popupService.findPopupsByNameWithPagination(null, lastPopupId, 3);

            // then
            Assertions.assertAll(
                    () -> assertThat(secondPage.content()).hasSize(2),
                    () -> assertThat(secondPage.isLast()).isTrue());
        }

        @Test
        @Transactional
        void 검색어에_대한_결과가_존재하면_결과_리스트를_반환한다() {
            // given
            LocalDate now = LocalDate.now();

            Long blackpinkPopup1 =
                    createPopupWithDates("블랙핑크 팝업스토어", now.minusDays(5), now.plusDays(10));
            Long blackpinkPopup2 =
                    createPopupWithDates("BLACKPINK 굿즈샵", now.minusDays(1), now.plusDays(5));
            createPopupWithDates("아이유 팝업", now.minusDays(1), now.plusDays(5));
            createPopupWithDates("BTS 콘서트", now, now.plusDays(15));
            createPopupWithDates("블랙핑크 종료팝업", now.minusDays(20), now.minusDays(1));

            // when
            SliceResponse<PopupInfoResponse> response =
                    popupService.findPopupsByNameWithPagination("블랙핑크", null, 10);

            // then
            Assertions.assertAll(
                    () -> assertThat(response.content()).hasSize(1), // trim() 적용시 "블랙핑크"만 매칭
                    () ->
                            assertThat(response.content().get(0).popupId())
                                    .isEqualTo(blackpinkPopup1),
                    () -> assertThat(response.content().get(0).popupName()).contains("블랙핑크"),
                    () -> assertThat(response.content().get(0).popupOpenDate()).isNotEmpty(),
                    () -> assertThat(response.content().get(0).popupCloseDate()).isNotEmpty(),
                    () -> assertThat(response.content().get(0).address()).isNotEmpty(),
                    () -> assertThat(response.isLast()).isTrue());
        }

        @Test
        @Transactional
        void 검색어에_대해_결과가_없으면_빈_리스트를_반환한다() {
            // given
            LocalDate now = LocalDate.now();

            createPopupWithDates("블랙핑크 팝업", now.minusDays(5), now.plusDays(10));
            createPopupWithDates("아이유 팝업", now.minusDays(1), now.plusDays(5));
            createPopupWithDates("BTS 콘서트", now, now.plusDays(15));

            // when
            SliceResponse<PopupInfoResponse> response =
                    popupService.findPopupsByNameWithPagination("존재하지않는팝업", null, 10);

            // then
            Assertions.assertAll(
                    () -> assertThat(response.content()).isEmpty(),
                    () -> assertThat(response.content()).hasSize(0),
                    () -> assertThat(response.isLast()).isTrue());
        }
    }

    @Nested
    class 팝업_설문지_조회할_때 {

        @Test
        @Transactional
        void 팝업_ID를_통해_설문지와_선지_조회에_성공한다() {
            // given
            Long popupId = createPopup();

            // when
            List<SurveyChoiceResponse> surveyChoices =
                    popupService.findAllChoicesByPopupId(popupId);

            // then
            assertThat(surveyChoices).hasSize(4);
            for (SurveyChoiceResponse choice : surveyChoices) {
                assertThat(choice.surveyId()).isNotNull();
                assertThat(choice.options()).hasSize(4);
            }
        }
    }

    @Nested
    class 팝업_상세_조회할_때 {

        @Test
        @Transactional
        void 사용자가_예약한_팝업_ID_리스트를_통해_상세_정보_조회에_성공한다() {
            // given
            Long popupId = createPopup();
            PopupIdsRequest request = new PopupIdsRequest(List.of(popupId));

            // when
            List<PopupDetailResponse> popupDetails = popupService.findPopupDetails(request);

            // then
            assertThat(popupDetails).hasSize(1);

            PopupDetailResponse popupDetail = popupDetails.get(0);
            assertThat(popupDetail.popupName()).isNotEmpty();
            assertThat(popupDetail.address()).isNotEmpty();
            assertThat(popupDetail.latitude()).isNotNull();
            assertThat(popupDetail.longitude()).isNotNull();
        }
    }

    private Long createPopupWithAllParams(
            String name,
            String imageUrl,
            LocalDate popupStartDate,
            LocalDate popupEndDate,
            LocalDateTime reservationOpenDateTime,
            LocalDateTime reservationCloseDateTime,
            LocalTime runOpenTime,
            LocalTime runCloseTime,
            int totalCapacity,
            int timeCapacity,
            String roadAddress,
            String detailAddress,
            Double latitude,
            Double longitude,
            List<ChoiceCreateRequest> choices) {

        PopupCreateRequest popupCreateRequest =
                new PopupCreateRequest(
                        name,
                        imageUrl,
                        popupStartDate,
                        popupEndDate,
                        reservationOpenDateTime,
                        reservationCloseDateTime,
                        runOpenTime,
                        runCloseTime,
                        totalCapacity,
                        timeCapacity,
                        roadAddress,
                        detailAddress,
                        latitude,
                        longitude);

        PopupWithChoicesCreateRequest request =
                new PopupWithChoicesCreateRequest(popupCreateRequest, choices);

        PopupCreateResponse response = popupService.createPopup(request);
        return response.popupId();
    }

    private Long createPopupWithDates(String name, LocalDate startDate, LocalDate endDate) {
        return createPopupWithAllParams(
                name,
                "https://bucket/image.jpg",
                startDate,
                endDate,
                LocalDateTime.of(startDate, LocalTime.of(10, 0)),
                LocalDateTime.of(endDate, LocalTime.of(20, 0)),
                LocalTime.of(10, 0),
                LocalTime.of(20, 0),
                100,
                20,
                "서울특별시 강남구 테헤란로 123",
                "3층 A호",
                37.123456,
                127.123456,
                createDefaultChoices());
    }

    private List<ChoiceCreateRequest> createDefaultChoices() {
        return List.of(
                new ChoiceCreateRequest(List.of("choice1", "choice2", "choice3", "choice4")),
                new ChoiceCreateRequest(List.of("choice1", "choice2", "choice3", "choice4")),
                new ChoiceCreateRequest(List.of("choice1", "choice2", "choice3", "choice4")),
                new ChoiceCreateRequest(List.of("choice1", "choice2", "choice3", "choice4")));
    }

    private PopupWithChoicesCreateRequest createPopupWithChoicesCreateRequest() {
        return new PopupWithChoicesCreateRequest(
                new PopupCreateRequest(
                        "popup1",
                        "https://bucket/asdf",
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

    private Long createPopup() {
        PopupCreateResponse popup = popupService.createPopup(createPopupWithChoicesCreateRequest());
        return popup.popupId();
    }
}
