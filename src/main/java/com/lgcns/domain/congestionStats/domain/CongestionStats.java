package com.lgcns.domain.congestionStats.domain;

import com.lgcns.domain.reservationStats.dto.response.DayOfWeek;
import com.lgcns.global.model.BaseTimeEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CongestionStats extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "congestion_stats_id")
    private Long id;

    private Long popupId;

    private int entrantCount;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private LocalDate analyzedDate;

    private LocalTime analyzedTime;

    @Builder
    private CongestionStats(
            Long popupId,
            int entrantCount,
            DayOfWeek dayOfWeek,
            LocalDate analyzedDate,
            LocalTime analyzedTime) {
        this.popupId = popupId;
        this.entrantCount = entrantCount;
        this.dayOfWeek = dayOfWeek;
        this.analyzedDate = analyzedDate;
        this.analyzedTime = analyzedTime;
    }

    public static CongestionStats createCongestionStats(
            Long popupId,
            int entrantCount,
            DayOfWeek dayOfWeek,
            LocalDate analyzedDate,
            LocalTime analyzedTime) {
        return CongestionStats.builder()
                .popupId(popupId)
                .entrantCount(entrantCount)
                .dayOfWeek(dayOfWeek)
                .analyzedDate(analyzedDate)
                .analyzedTime(analyzedTime)
                .build();
    }
}
