package com.lgcns.domain.paymentStats.domain;

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
public class PaymentStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_stats_id")
    private Long id;

    @Column(name = "popup_id")
    private Long popupId;

    private int averageAmount;

    @Enumerated(EnumType.STRING)
    private AveragePeriod period;

    @Column(name = "analyzed_date")
    private LocalDate analyzedDate;

    @Column(name = "analyzed_time")
    private LocalTime analyzedTime;

    @Builder(access = AccessLevel.PRIVATE)
    public PaymentStats(
            Long popupId,
            int averageAmount,
            AveragePeriod period,
            LocalDate analyzedDate,
            LocalTime analyzedTime) {
        this.popupId = popupId;
        this.averageAmount = averageAmount;
        this.period = period;
        this.analyzedDate = analyzedDate;
        this.analyzedTime = analyzedTime;
    }

    public static PaymentStats createPaymentStats(
            Long popupId,
            int averageAmount,
            AveragePeriod period,
            LocalDate analyzedDate,
            LocalTime analyzedTime) {
        return PaymentStats.builder()
                .popupId(popupId)
                .averageAmount(averageAmount)
                .period(period)
                .analyzedDate(analyzedDate)
                .analyzedTime(analyzedTime)
                .build();
    }
}
