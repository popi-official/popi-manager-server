package com.lgcns.domain.popup.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Popup {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String imageUrl;

    private LocalDate popupStartDate;
    private LocalDate popupEndDate;

    private LocalDateTime reservationOpenDateTime;
    private LocalDateTime reservationCloseDateTime;

    private LocalTime runOpenTime;
    private LocalTime runCloseTime;

    @Embedded private PopupAddress address;

    @Builder
    private Popup(
            String name,
            String imageUrl,
            LocalDate popupStartDate,
            LocalDate popupEndDate,
            LocalDateTime reservationOpenDateTime,
            LocalDateTime reservationCloseDateTime,
            LocalTime runOpenTime,
            LocalTime runCloseTime,
            PopupAddress address) {

        this.name = name;
        this.imageUrl = imageUrl;
        this.popupStartDate = popupStartDate;
        this.popupEndDate = popupEndDate;
        this.reservationOpenDateTime = reservationOpenDateTime;
        this.reservationCloseDateTime = reservationCloseDateTime;
        this.runOpenTime = runOpenTime;
        this.runCloseTime = runCloseTime;
        this.address = address;
    }

    public static Popup createPopup(
            String name,
            String imageUrl,
            LocalDate popupStartDate,
            LocalDate popupEndDate,
            LocalDateTime reservationOpenDateTime,
            LocalDateTime reservationCloseDateTime,
            LocalTime runOpenTime,
            LocalTime runCloseTime,
            String roadAddress,
            String detailAddress,
            Double latitude,
            Double longitude) {

        PopupAddress address =
                PopupAddress.createPopupAddress(roadAddress, detailAddress, latitude, longitude);

        return Popup.builder()
                .name(name)
                .imageUrl(imageUrl)
                .popupStartDate(popupStartDate)
                .popupEndDate(popupEndDate)
                .reservationOpenDateTime(reservationOpenDateTime)
                .reservationCloseDateTime(reservationCloseDateTime)
                .runOpenTime(runOpenTime)
                .runCloseTime(runCloseTime)
                .address(address)
                .build();
    }
}
