package com.lgcns.domain.visitLog.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.visitLog.domain.Entrance;
import com.lgcns.domain.visitLog.domain.UserGender;
import com.lgcns.domain.visitLog.dto.request.EntranceCreateRequest;
import com.lgcns.domain.visitLog.repository.EntranceRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class VisitLogServiceTest extends IntegrationTest {

    @Autowired private VisitLogService visitLogService;

    @Autowired private EntranceRepository entranceRepository;

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

            EntranceCreateRequest request =
                    new EntranceCreateRequest(popupId, gender, ageGroup, date, time);

            // when
            visitLogService.createEntrance(request);

            // then
            Entrance savedEntrance = entranceRepository.findAll().get(0);
            Assertions.assertAll(
                    () -> assertThat(savedEntrance.getPopupId()).isEqualTo(1L),
                    () -> assertThat(savedEntrance.getGender()).isEqualTo(UserGender.MALE),
                    () -> assertThat(savedEntrance.getAgeGroup()).isEqualTo(20),
                    () ->
                            assertThat(savedEntrance.getDate())
                                    .isEqualTo(LocalDate.parse("2025-05-13")),
                    () ->
                            assertThat(savedEntrance.getTime())
                                    .isEqualTo(LocalTime.parse("10:00:00")));
        }
    }
}
