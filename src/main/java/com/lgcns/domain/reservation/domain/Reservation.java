package com.lgcns.domain.reservation.domain;

import com.lgcns.domain.popup.domain.Popup;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id")
    private Popup popup;

    private LocalDate reservationDate;
    private LocalTime reservationTime;

    private int possibleCount;

    private LocalDateTime reservationOpenDateTime;
    private LocalDateTime reservationCloseDateTime;

    @Builder
    private Reservation(
            Popup popup,
            LocalDate reservationDate,
            LocalTime reservationTime,
            int possibleCount,
            LocalDateTime reservationOpenDateTime,
            LocalDateTime reservationCloseDateTime) {
        this.popup = popup;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.possibleCount = possibleCount;
        this.reservationOpenDateTime = reservationOpenDateTime;
        this.reservationCloseDateTime = reservationCloseDateTime;
    }

    public static Reservation createReservation(
            Popup popup,
            LocalDate reservationDate,
            LocalTime reservationTime,
            int possibleCount,
            LocalDateTime reservationOpenDateTime,
            LocalDateTime reservationCloseDateTime) {
        return Reservation.builder()
                .popup(popup)
                .reservationDate(reservationDate)
                .reservationTime(reservationTime)
                .possibleCount(possibleCount)
                .reservationOpenDateTime(reservationOpenDateTime)
                .reservationCloseDateTime(reservationCloseDateTime)
                .build();
    }
}
