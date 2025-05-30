package com.lgcns.domain.entrance.dto.request;

import com.lgcns.domain.entrance.domain.MemberAge;
import com.lgcns.domain.entrance.domain.MemberGender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record EntranceCreateRequest(
        @NotNull(message = "팝업 ID는 필수입니다.") @Schema(description = "팝업 ID", example = "1")
                Long popupId,
        @NotNull(message = "방문자 성별은 필수입니다.") @Schema(description = "방문자 성별", example = "FEMALE")
                MemberGender gender,
        @NotNull(message = "방문자 나이대는 필수입니다.") @Schema(description = "방문자 나이대", example = "20")
                MemberAge age,
        @NotNull(message = "예약 날짜는 필수입니다.")
                @Schema(description = "팝업 예약 날짜", example = "2025-05-13")
                @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 yyyy-MM-dd이어야 합니다.")
                String reservationDate,
        @NotNull(message = "예약 시간은 필수입니다.")
                @Schema(description = "팝업 예약 시간", example = "10:00:00")
                @Pattern(regexp = "^\\d{2}:\\d{2}:\\d{2}$", message = "시간 형식은 HH:mm:ss이어야 합니다.")
                String reservationTime) {
    public static EntranceCreateRequest of(
            Long popupId,
            MemberGender gender,
            MemberAge age,
            String reservationDate,
            String reservationTime) {
        return new EntranceCreateRequest(popupId, gender, age, reservationDate, reservationTime);
    }
}
