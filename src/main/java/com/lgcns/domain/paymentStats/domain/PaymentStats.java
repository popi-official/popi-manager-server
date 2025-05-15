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

    @Column(name = "analyzed_date")
    private LocalDate analyzedDate;

    @Column(name = "analyzed_time")
    private LocalTime analyzedTime;

    private int totalPayment;

    private int userCount;

    @Builder(access = AccessLevel.PRIVATE)
    public PaymentStats(
            Long popupId,
            LocalDate analyzedDate,
            LocalTime analyzedTime,
            int totalPayment,
            int userCount) {
        this.popupId = popupId;
        this.analyzedDate = analyzedDate;
        this.analyzedTime = analyzedTime;
        this.totalPayment = totalPayment;
        this.userCount = userCount;
    }

    public static PaymentStats createPaymentStats(
            Long popupId,
            LocalDate analyzedDate,
            LocalTime analyzedTime,
            int totalPayment,
            int userCount) {
        return PaymentStats.builder()
                .popupId(popupId)
                .analyzedDate(analyzedDate)
                .analyzedTime(analyzedTime)
                .totalPayment(totalPayment)
                .userCount(userCount)
                .build();
    }
}
