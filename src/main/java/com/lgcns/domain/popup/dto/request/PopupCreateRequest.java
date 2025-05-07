package com.lgcns.domain.popup.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record PopupCreateRequest(
        @Schema(description = "팝업스토어 이름", example = "BLACKPINK 팝업스토어", required = true)
                @NotBlank(message = "팝업스토어 이름은 필수입니다.")
                String name,
        @Schema(description = "팝업스토어 이미지 URL", example = "https://bucket/asdf", required = true)
                @NotBlank(message = "이미지 URL은 필수입니다.")
                String imageUrl,
        @Schema(description = "팝업스토어 시작 날짜", example = "2025-01-01", required = true)
                @NotNull(message = "팝업스토어 시작일은 필수입니다.")
                @JsonFormat(
                        shape = JsonFormat.Shape.STRING,
                        pattern = "yyyy-MM-dd",
                        timezone = "Asia/Seoul")
                LocalDate popupStartDate,
        @Schema(description = "팝업스토어 종료 날짜", example = "2025-01-31", required = true)
                @NotNull(message = "팝업스토어 종료일은 필수입니다.")
                @JsonFormat(
                        shape = JsonFormat.Shape.STRING,
                        pattern = "yyyy-MM-dd",
                        timezone = "Asia/Seoul")
                LocalDate popupEndDate,
        @Schema(description = "예약 시작일시", example = "2025-01-01T10:00:00", required = true)
                @NotNull(message = "예약 시작일시는 필수입니다.")
                @JsonFormat(
                        shape = JsonFormat.Shape.STRING,
                        pattern = "yyyy-MM-dd'T'HH:mm:ss",
                        timezone = "Asia/Seoul")
                LocalDateTime reservationOpenDateTime,
        @Schema(description = "예약 종료일시", example = "2025-01-31T20:00:00", required = true)
                @NotNull(message = "예약 종료일시는 필수입니다.")
                @JsonFormat(
                        shape = JsonFormat.Shape.STRING,
                        pattern = "yyyy-MM-dd'T'HH:mm:ss",
                        timezone = "Asia/Seoul")
                LocalDateTime reservationCloseDateTime,
        @Schema(description = "운영 시작시간", example = "10:00:00", required = true)
                @NotNull(message = "운영 시작시간은 필수입니다.")
                LocalTime runOpenTime,
        @Schema(description = "운영 종료시간", example = "20:00:00", required = true)
                @NotNull(message = "운영 종료시간은 필수입니다.")
                LocalTime runCloseTime,
        @Schema(description = "총 수용 인원", example = "100", required = true)
                @NotNull(message = "총 수용 인원은 필수입니다.")
                int totalCapacity,
        @Schema(description = "시간당 수용 인원", example = "20", required = true)
                @NotNull(message = "시간당 수용 인원은 필수입니다.")
                int timeCapacity,
        @Schema(description = "도로명 주소", example = "서울특별시 영등포구 여의대로 108", required = true)
                @NotBlank(message = "도로명 주소는 필수입니다.")
                String roadAddress,
        @Schema(description = "상세 주소", example = "5층", required = true)
                @NotBlank(message = "상세 주소는 필수입니다.")
                String detailAddress,
        @Schema(description = "위도", example = "37.123456", required = true)
                @NotNull(message = "위도는 필수입니다.")
                Double latitude,
        @Schema(description = "경도", example = "127.123456", required = true)
                @NotNull(message = "경도는 필수입니다.")
                Double longitude) {}
