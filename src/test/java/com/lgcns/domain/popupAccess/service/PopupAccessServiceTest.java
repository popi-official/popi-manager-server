package com.lgcns.domain.popupAccess.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.popupAccess.domain.PopupEnter;
import com.lgcns.domain.popupAccess.domain.UserGender;
import com.lgcns.domain.popupAccess.dto.request.PopupEnterCreateRequest;
import com.lgcns.domain.popupAccess.repository.PopupEnterRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class PopupAccessServiceTest extends IntegrationTest {

    @Autowired private PopupAccessService popupAccessService;

    @Autowired private PopupEnterRepository popupEnterRepository;

    @Nested
    class 방문자_팝업_입장할_때 {

        @Test
        @Transactional
        void 방문자_입장시_등록에_성공한다() {
            // given
            Long popupId = 1L;
            UserGender gender = UserGender.MALE;
            int ageGroup = 20;
            LocalDate date = LocalDate.parse("2025-05-13");
            LocalTime time = LocalTime.parse("10:00:00");

            PopupEnterCreateRequest request =
                    new PopupEnterCreateRequest(gender, ageGroup, date, time);

            // when
            popupAccessService.createPopupEnter(popupId, request);

            // then
            PopupEnter savedPopupEnter = popupEnterRepository.findAll().get(0);
            Assertions.assertAll(
                    () -> assertThat(savedPopupEnter.getPopupId()).isEqualTo(1L),
                    () -> assertThat(savedPopupEnter.getGender()).isEqualTo(UserGender.MALE),
                    () -> assertThat(savedPopupEnter.getAgeGroup()).isEqualTo(20),
                    () ->
                            assertThat(savedPopupEnter.getDate())
                                    .isEqualTo(LocalDate.parse("2025-05-13")),
                    () ->
                            assertThat(savedPopupEnter.getTime())
                                    .isEqualTo(LocalTime.parse("10:00:00")));
        }
    }
}
