package com.lgcns.domain.survey.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.survey.domain.Choice;
import com.lgcns.domain.survey.domain.MemberAnswer;
import com.lgcns.domain.survey.domain.Survey;
import com.lgcns.domain.survey.dto.response.ChoiceStatsResponse;
import com.lgcns.domain.survey.dto.response.SurveyAnalysisResponse;
import com.lgcns.domain.survey.repository.ChoiceRepository;
import com.lgcns.domain.survey.repository.MemberAnswerRepository;
import com.lgcns.domain.survey.repository.SurveyRepository;
import com.lgcns.global.error.exception.CustomException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SurveyServiceTest extends IntegrationTest {
    @Autowired SurveyService surveyService;
    @Autowired SurveyRepository surveyRepository;
    @Autowired ChoiceRepository choiceRepository;
    @Autowired MemberAnswerRepository memberAnswerRepository;
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
    class 설문지_분석_조회 {
        @Test
        @Transactional
        void 설문지_분석_조회에_성공한다() {
            // given
            final Long popupId = popup.getId();
            createSurvey(popup);

            // when
            SurveyAnalysisResponse surveyAnalysisResponse =
                    surveyService.getSurveyAnalysis(popupId);

            // then
            assertAll(
                    () -> assertEquals(1L, surveyAnalysisResponse.totalCount()), // 총 응답자 수
                    () -> assertEquals(4, surveyAnalysisResponse.surveys().size()), // Survey 개수
                    () ->
                            surveyAnalysisResponse
                                    .surveys()
                                    .forEach(
                                            survey ->
                                                    assertEquals(
                                                            4,
                                                            survey.contents()
                                                                    .size())), // 각 Survey의 Choice
                    // 개수 확인
                    () ->
                            surveyAnalysisResponse
                                    .surveys()
                                    .forEach(
                                            survey -> {
                                                List<ChoiceStatsResponse> choices =
                                                        survey.contents();
                                                for (ChoiceStatsResponse choice : choices) {
                                                    if (choice.title().equals("보기1")) {
                                                        assertEquals(1, choice.selectedCount());
                                                        assertEquals(100, choice.ratio());
                                                    } else {
                                                        assertEquals(0, choice.selectedCount());
                                                        assertEquals(0, choice.ratio());
                                                    }
                                                }
                                            }) // 각 Survey의 첫 번째 Choice만 (count 1, ratio 100), 나머지는
                    // 0
                    );
        }

        @Test
        @Transactional
        void 권한이_없는_사용자가_설문지_분석을_조회하면_예외가_발생한다() {
            // given
            setAuthentication(otherManager);
            final Long popupId = popup.getId();

            // when & then
            assertThatThrownBy(() -> surveyService.getSurveyAnalysis(popupId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_UNAUTHORIZED);
        }

        @Test
        @Transactional
        void 존재하지_않는_팝업에_대해_설문지_분석을_조회하면_예외가_발생한다() {
            // given
            final Long popupId = 9999L;

            // when & then
            assertThatThrownBy(() -> surveyService.getSurveyAnalysis(popupId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_NOT_FOUND);
        }
    }

    private void createSurvey(Popup popup) {
        // Survey 4개 생성
        List<Survey> surveys = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Survey survey = Survey.createSurvey(popup, i);
            surveys.add(surveyRepository.save(survey));
        }

        // 각 Survey마다 Choice 4개 생성
        for (Survey survey : surveys) {
            for (int i = 1; i <= 4; i++) {
                Choice choice = Choice.createChoice(survey, "보기" + i, i);
                choiceRepository.save(choice);
            }
        }

        // 1명의 사용자 응답 (Survey 4개에 대해 각 1개씩 응답)
        for (Survey survey : surveys) {
            // 예: 선택 번호는 무조건 1번으로 고정 (또는 랜덤 가능)
            MemberAnswer answer =
                    MemberAnswer.builder()
                            .survey(survey)
                            .answerNumber(1)
                            .memberGender("MALE")
                            .memberAge(25)
                            .build();
            memberAnswerRepository.save(answer);
        }
    }
}
