package com.lgcns.domain.popup.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.dto.request.PopupCreateRequest;
import com.lgcns.domain.popup.dto.request.PopupWithChoicesCreateRequest;
import com.lgcns.domain.popup.dto.response.PopupCreateResponse;
import com.lgcns.domain.popup.dto.response.PopupPreviewResponse;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.survey.domain.Choice;
import com.lgcns.domain.survey.domain.Survey;
import com.lgcns.domain.survey.dto.request.ChoiceCreateRequest;
import com.lgcns.domain.survey.repository.ChoiceRepository;
import com.lgcns.domain.survey.repository.SurveyRepository;
import com.lgcns.global.security.PrincipalDetails;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

public class PopupServiceTest extends IntegrationTest {
    @Autowired PopupService popupService;
    @Autowired PopupRepository popupRepository;
    @Autowired SurveyRepository surveyRepository;

    @Autowired private ManagerRepository managerRepository;
    @Autowired private ChoiceRepository choiceRepository;

    private Manager manager;

    @BeforeEach
    void setUp() {
        manager = managerRepository.save(Manager.createManager("testUsername", "testPassword"));

        UserDetails userDetails = new PrincipalDetails(manager.getId(), manager.getRole(), null);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
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
            Popup popup = popupRepository.findAll().get(0);
            Assertions.assertAll(
                    () -> assertThat(response.popupId()).isEqualTo(popup.getId()),
                    () -> assertThat(popup.getName()).isEqualTo("popup1"),
                    () -> assertThat(popup.getImageUrl()).isEqualTo("https://bucket/asdf"),
                    () ->
                            assertThat(popup.getPopupStartDate())
                                    .isEqualTo(LocalDate.parse("2025-01-01")),
                    () ->
                            assertThat(popup.getPopupEndDate())
                                    .isEqualTo(LocalDate.parse("2025-01-31")));

            List<Survey> surveyList = surveyRepository.findAll();
            assertThat(surveyList).hasSize(4);

            List<Choice> choiceList = choiceRepository.findAll();
            assertThat(choiceList).hasSize(16);
        }
    }

    @Nested
    class 팝업_목록_조회 {
        @Test
        @Transactional
        void 팝업_목록_성공한다() {
            // given
            PopupWithChoicesCreateRequest popupWithChoicesCreateRequest =
                    createPopupWithChoicesCreateRequest();
            popupService.createPopup(popupWithChoicesCreateRequest);
            popupService.createPopup(popupWithChoicesCreateRequest);

            // when
            List<PopupPreviewResponse> popupPreviewResponseList = popupService.findAllPopups();

            // then
            assertThat(popupPreviewResponseList).hasSize(2);
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
}
