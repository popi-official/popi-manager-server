package com.lgcns.domain.popupAccess.dto.request;

import com.lgcns.domain.popupAccess.domain.UserGender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record PopupEnterCreateRequest(
        @NotNull(message = "방문자 성별은 필수입니다.") @Schema(description = "방문자 성별", example = "FEMALE")
                UserGender gender,
        @NotNull(message = "방문자 나이대는 필수입니다.") @Schema(description = "방문자 나이대", example = "20")
                int ageGroup,
        @NotNull(message = "예약 날짜는 필수입니다.")
                @Schema(description = "팝업 예약 날짜", example = "2025-05-13")
                LocalDate date,
        @NotNull(message = "예약 시간은 필수입니다.") @Schema(description = "팝업 예약 시간", example = "10:00:00")
                LocalTime time) {
    public static PopupEnterCreateRequest of(
            UserGender gender, int ageGroup, LocalDate date, LocalTime time) {
        return new PopupEnterCreateRequest(gender, ageGroup, date, time);
    }
}
