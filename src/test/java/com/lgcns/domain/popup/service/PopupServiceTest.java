package com.lgcns.domain.popup.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.item.service.ItemService;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.dto.request.PopupCreateRequest;
import com.lgcns.domain.popup.dto.request.PopupWithChoicesCreateRequest;
import com.lgcns.domain.popup.dto.response.PopupCreateResponse;
import com.lgcns.domain.popup.dto.response.PopupPreviewResponse;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.survey.domain.Choice;
import com.lgcns.domain.survey.domain.Survey;
import com.lgcns.domain.survey.dto.request.ChoiceCreateRequest;
import com.lgcns.domain.survey.repository.ChoiceRepository;
import com.lgcns.domain.survey.repository.SurveyRepository;
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
    @Autowired private ItemService itemService;
    @Autowired private PopupRepository popupRepository;
    @Autowired private SurveyRepository surveyRepository;

    @Autowired private ManagerRepository managerRepository;
    @Autowired private ChoiceRepository choiceRepository;
    @Autowired private ItemRepository itemRepository;

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
            Assertions.assertAll(
                    () -> assertThat(response.popupId()).isEqualTo(savedPopup.getId()),
                    () -> assertThat(savedPopup.getName()).isEqualTo("popup1"),
                    () -> assertThat(savedPopup.getImageUrl()).isEqualTo("https://bucket/asdf"),
                    () ->
                            assertThat(savedPopup.getPopupStartDate())
                                    .isEqualTo(LocalDate.parse("2025-01-01")),
                    () ->
                            assertThat(savedPopup.getPopupEndDate())
                                    .isEqualTo(LocalDate.parse("2025-01-31")));

            List<Survey> surveyList = surveyRepository.findAll();
            assertThat(surveyList).hasSize(4); // 기존 팝업 + 새로 생성된 팝업의 설문들

            List<Choice> choiceList = choiceRepository.findAll();
            assertThat(choiceList).hasSize(16);
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
