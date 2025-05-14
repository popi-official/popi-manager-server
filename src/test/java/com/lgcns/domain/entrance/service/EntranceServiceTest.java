package com.lgcns.domain.entrance.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.entrance.domain.Entrance;
import com.lgcns.domain.entrance.domain.MemberGender;
import com.lgcns.domain.entrance.dto.request.EntranceCreateRequest;
import com.lgcns.domain.entrance.repository.EntranceRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class EntranceServiceTest extends IntegrationTest {

    @Autowired private EntranceService entranceService;

    @Autowired private EntranceRepository entranceRepository;

    @Nested
    class 방문자_팝업_입장할_때 {

        @Test
        @Transactional
        void 방문자_입장시_등록에_성공한다() {
            // given
            Long popupId = 1L;
            MemberGender gender = MemberGender.MALE;
            int ageGroup = 20;
            LocalDate date = LocalDate.parse("2025-05-13");
            LocalTime time = LocalTime.parse("10:00:00");

            EntranceCreateRequest request =
                    new EntranceCreateRequest(popupId, gender, ageGroup, date, time);

            // when
            entranceService.createEntrance(request);

            // then
            Entrance savedEntrance = entranceRepository.findAll().get(0);
            Assertions.assertAll(
                    () -> assertThat(savedEntrance.getPopupId()).isEqualTo(1L),
                    () -> assertThat(savedEntrance.getGender()).isEqualTo(MemberGender.MALE),
                    () -> assertThat(savedEntrance.getAgeGroup()).isEqualTo(20),
                    () ->
                            assertThat(savedEntrance.getReservationDate())
                                    .isEqualTo(LocalDate.parse("2025-05-13")),
                    () ->
                            assertThat(savedEntrance.getReservationTime())
                                    .isEqualTo(LocalTime.parse("10:00:00")));
        }
    }
}
