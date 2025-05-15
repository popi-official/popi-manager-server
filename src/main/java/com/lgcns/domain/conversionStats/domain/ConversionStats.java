package com.lgcns.domain.conversionStats.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
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
}
