package com.lgcns.domain.entrance.dto.response;

import com.lgcns.domain.entrance.domain.MemberGender;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

public record HourlyEntranceResponse(
        @Schema(description = "성별", example = "MALE") MemberGender gender,
        @Schema(description = "나이대", example = "20") Integer ageGroup,
        @Schema(description = "예약날짜", example = "2025-05-29") LocalDate reservationDate,
        @Schema(description = "예약시간", example = "10:00:00") LocalTime reservationTime) {
    public static HourlyEntranceResponse of(
            MemberGender gender,
            Integer ageGroup,
            LocalDate reservationDate,
            LocalTime reservationTime) {
        return new HourlyEntranceResponse(gender, ageGroup, reservationDate, reservationTime);
    }
}
