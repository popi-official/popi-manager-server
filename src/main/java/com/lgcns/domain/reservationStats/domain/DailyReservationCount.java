package com.lgcns.domain.reservationStats.domain;

import com.lgcns.global.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class DailyReservationCount extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_reservation_count_id")
    private Long id;

    private Long popupId;

    private int reservationCount;

    @Builder
    private DailyReservationCount(Long popupId, int reservationCount) {
        this.popupId = popupId;
        this.reservationCount = reservationCount;
    }

    public static DailyReservationCount createDailyReservationCount(
            Long popupId, int reservationCount) {
        return DailyReservationCount.builder()
                .popupId(popupId)
                .reservationCount(reservationCount)
                .build();
    }
}
