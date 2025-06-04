package com.lgcns.domain.conversionStats.domain;

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
public class ConversionStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversion_stats_id")
    private Long id;

    private Long popupId;
    private Long itemId;

    private int interestedCount;
    private int buyerCount;
    private int conversionRate;

    private LocalDate analyzedDate;
    private LocalTime analyzedTime;

    @Builder(access = AccessLevel.PRIVATE)
    public ConversionStats(
            Long popupId,
            Long itemId,
            int interestedCount,
            int buyerCount,
            int conversionRate,
            LocalDate analyzedDate,
            LocalTime analyzedTime) {
        this.popupId = popupId;
        this.itemId = itemId;
        this.interestedCount = interestedCount;
        this.buyerCount = buyerCount;
        this.conversionRate = conversionRate;
        this.analyzedDate = analyzedDate;
        this.analyzedTime = analyzedTime;
    }

    public static ConversionStats createConversionStats(
            Long popupId,
            Long itemId,
            int interestedCount,
            int buyerCount,
            int conversionRate,
            LocalDate analyzedDate,
            LocalTime analyzedTime) {
        return ConversionStats.builder()
                .popupId(popupId)
                .itemId(itemId)
                .interestedCount(interestedCount)
                .buyerCount(buyerCount)
                .conversionRate(conversionRate)
                .analyzedDate(analyzedDate)
                .analyzedTime(analyzedTime)
                .build();
    }
}
