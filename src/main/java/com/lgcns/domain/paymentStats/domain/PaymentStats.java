package com.lgcns.domain.paymentStats.domain;

import com.lgcns.domain.popup.domain.Popup;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id")
    private Popup popup;

    private LocalDate date;

    private LocalTime time;

    private int totalPayment;

    private int userCount;

    @Builder(access = AccessLevel.PRIVATE)
    public PaymentStats(
            Popup popup, LocalDate date, LocalTime time, int totalPayment, int userCount) {
        this.popup = popup;
        this.date = date;
        this.time = time;
        this.totalPayment = totalPayment;
        this.userCount = userCount;
    }

    public static PaymentStats createPaymentStats(
            Popup popup, LocalDate date, LocalTime time, int totalPayment, int userCount) {
        return PaymentStats.builder()
                .popup(popup)
                .date(date)
                .time(time)
                .totalPayment(totalPayment)
                .userCount(userCount)
                .build();
    }
}
