package com.lgcns.domain.popup.domain;

import com.lgcns.domain.survey.domain.Survey;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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

    private int totalCapacity;
    private int timeCapacity;

    @Embedded private PopupAddress address;

    @OneToMany(mappedBy = "popup", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Survey> surveyList = new ArrayList<>();

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
            int totalCapacity,
            int timeCapacity,
            PopupAddress address) {

        this.name = name;
        this.imageUrl = imageUrl;
        this.popupStartDate = popupStartDate;
        this.popupEndDate = popupEndDate;
        this.reservationOpenDateTime = reservationOpenDateTime;
        this.reservationCloseDateTime = reservationCloseDateTime;
        this.runOpenTime = runOpenTime;
        this.runCloseTime = runCloseTime;
        this.totalCapacity = totalCapacity;
        this.timeCapacity = timeCapacity;
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
            int totalCapacity,
            int timeCapacity,
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
                .totalCapacity(totalCapacity)
                .timeCapacity(timeCapacity)
                .address(address)
                .build();
    }
}
