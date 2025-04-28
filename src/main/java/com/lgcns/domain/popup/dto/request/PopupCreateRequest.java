package com.lgcns.domain.popup.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record PopupCreateRequest(
        @NotNull(message = "팝업스토어 이름은 필수입니다.") String name,
        @NotNull(message = "이미지 URL은 필수입니다.") String imageUrl,
        @NotNull(message = "팝업스토어 시작일은 필수입니다.")
                @JsonFormat(
                        shape = JsonFormat.Shape.STRING,
                        pattern = "yyyy-MM-dd",
                        timezone = "Asia/Seoul")
                LocalDate popupStartDate,
        @NotNull(message = "팝업스토어 종료일은 필수입니다.")
                @JsonFormat(
                        shape = JsonFormat.Shape.STRING,
                        pattern = "yyyy-MM-dd",
                        timezone = "Asia/Seoul")
                LocalDate popupEndDate,
        @NotNull(message = "예약 시작일시는 필수입니다.")
                @JsonFormat(
                        shape = JsonFormat.Shape.STRING,
                        pattern = "yyyy-MM-dd'T'HH:mm:ss",
                        timezone = "Asia/Seoul")
                LocalDateTime reservationOpenDateTime,
        @NotNull(message = "예약 종료일시는 필수입니다.")
                @JsonFormat(
                        shape = JsonFormat.Shape.STRING,
                        pattern = "yyyy-MM-dd'T'HH:mm:ss",
                        timezone = "Asia/Seoul")
                LocalDateTime reservationCloseDateTime,
        @NotNull(message = "운영 시작시간은 필수입니다.") LocalTime runOpenTime,
        @NotNull(message = "운영 종료시간은 필수입니다.") LocalTime runCloseTime,
        @NotNull(message = "총 수용 인원은 필수입니다.") int totalCapacity,
        @NotNull(message = "시간당 수용 인원은 필수입니다.") int timeCapacity,
        @NotNull(message = "도로명 주소는 필수입니다.") String roadAddress,
        @NotNull(message = "상세 주소는 필수입니다.") String detailAddress,
        @NotNull(message = "위도는 필수입니다.") Double latitude,
        @NotNull(message = "경도는 필수입니다.") Double longitude) {}
