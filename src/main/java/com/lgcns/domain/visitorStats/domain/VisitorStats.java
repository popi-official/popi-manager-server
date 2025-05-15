package com.lgcns.domain.visitorStats.domain;

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
public class VisitorStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visitor_stats_id")
    private Long id;

    private Long popupId;

    private int maleCount;

    private int femaleCount;

    private int teenCount;

    private int twentyCount;

    private int thirtyCount;

    private int fortyCount;

    private LocalDate analyzedDate;

    private LocalTime analyzedTime;

    @Builder
    private VisitorStats(
            Long popupId,
            int maleCount,
            int femaleCount,
            int teenCount,
            int twentyCount,
            int thirtyCount,
            int fortyCount,
            LocalDate analyzedDate,
            LocalTime analyzedTime) {

        this.popupId = popupId;
        this.maleCount = maleCount;
        this.femaleCount = femaleCount;
        this.teenCount = teenCount;
        this.twentyCount = twentyCount;
        this.thirtyCount = thirtyCount;
        this.fortyCount = fortyCount;
        this.analyzedDate = analyzedDate;
        this.analyzedTime = analyzedTime;
    }

    public static VisitorStats createVisitorStats(
            Long popupId,
            int maleCount,
            int femaleCount,
            int teenCount,
            int twentyCount,
            int thirtyCount,
            int fortyCount,
            LocalDate analyzedDate,
            LocalTime analyzedTime) {
        return VisitorStats.builder()
                .popupId(popupId)
                .maleCount(maleCount)
                .femaleCount(femaleCount)
                .teenCount(teenCount)
                .twentyCount(twentyCount)
                .thirtyCount(thirtyCount)
                .fortyCount(fortyCount)
                .analyzedDate(analyzedDate)
                .analyzedTime(analyzedTime)
                .build();
    }
}
