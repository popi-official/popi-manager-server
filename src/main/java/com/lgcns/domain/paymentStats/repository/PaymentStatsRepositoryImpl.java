package com.lgcns.domain.paymentStats.repository;

import static com.lgcns.domain.paymentStats.domain.QPaymentStats.paymentStats;

import com.lgcns.domain.paymentStats.dto.response.PaymentAverageResponse;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentStatsRepositoryImpl implements PaymentStatsRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public PaymentAverageResponse findAveragePayment(
            Long popupId, LocalDate openDate, LocalDate today, LocalTime currentTime) {
        NumberExpression<Double> totalAverage =
                new CaseBuilder()
                        .when(paymentStats.userCount.sum().eq(0))
                        .then(0.0)
                        .otherwise(
                                paymentStats
                                        .totalPayment
                                        .sum()
                                        .doubleValue()
                                        .divide(paymentStats.userCount.sum().doubleValue()));

        NumberExpression<Double> todayAverage =
                new CaseBuilder()
                        .when(paymentStats.userCount.sum().eq(0))
                        .then(0.0)
                        .otherwise(
                                paymentStats
                                        .totalPayment
                                        .sum()
                                        .doubleValue()
                                        .divide(paymentStats.userCount.sum().doubleValue()));

        Double totalPaymentAvg =
                queryFactory
                        .select(totalAverage)
                        .from(paymentStats)
                        .where(
                                paymentStats.popup.id.eq(popupId),
                                paymentStats.date.goe(openDate),
                                paymentStats
                                        .date
                                        .lt(today)
                                        .or(
                                                paymentStats
                                                        .date
                                                        .eq(today)
                                                        .and(paymentStats.time.lt(currentTime))))
                        .fetchOne();

        Double todayPaymentAvg =
                queryFactory
                        .select(todayAverage)
                        .from(paymentStats)
                        .where(
                                paymentStats.popup.id.eq(popupId),
                                paymentStats.date.eq(today),
                                paymentStats.time.lt(currentTime))
                        .fetchOne();

        return new PaymentAverageResponse(
                String.valueOf(Math.round(totalPaymentAvg != null ? totalPaymentAvg : 0)),
                String.valueOf(Math.round(todayPaymentAvg != null ? todayPaymentAvg : 0)));
    }
}
