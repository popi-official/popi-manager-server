package com.lgcns.domain.entrance.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

public record HourlyEntranceResponse(
        @Schema(description = "남성 수", example = "25") Integer maleCount,
        @Schema(description = "여성 수", example = "25") Integer femaleCount,
        @Schema(description = "10대 수", example = "15") Integer teenCount,
        @Schema(description = "20대 수", example = "15") Integer twentyCount,
        @Schema(description = "30대 수", example = "10") Integer thirtyCount,
        @Schema(description = "40대 수", example = "10") Integer fortyCount,
        @Schema(description = "예약날짜", example = "2025-05-29") LocalDate reservationDate,
        @Schema(description = "예약시간", example = "10:00:00") LocalTime reservationTime) {
    public static HourlyEntranceResponse of(
            Integer maleCount,
            Integer femaleCount,
            Integer teenCount,
            Integer twentyCount,
            Integer thirtyCount,
            Integer fortyCount,
            LocalDate reservationDate,
            LocalTime reservationTime) {
        return new HourlyEntranceResponse(
                maleCount,
                femaleCount,
                teenCount,
                twentyCount,
                thirtyCount,
                fortyCount,
                reservationDate,
                reservationTime);
    }
}
