package com.lgcns.domain.paymentStats.repository;

import static com.lgcns.domain.paymentStats.domain.QPaymentStats.paymentStats;

import com.lgcns.domain.paymentStats.dto.response.PaymentAverageResponse;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentStatsRepositoryImpl implements PaymentStatsRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public PaymentAverageResponse getPaymentAverages(Long popupId, LocalDate today) {
        NumberExpression<Double> avgExpression =
                new CaseBuilder()
                        .when(paymentStats.userCount.sum().eq(0))
                        .then(0.0)
                        .otherwise(
                                paymentStats
                                        .totalPayment
                                        .sum()
                                        .doubleValue()
                                        .divide(paymentStats.userCount.sum().doubleValue()));

        Double totalAvg =
                queryFactory
                        .select(avgExpression)
                        .from(paymentStats)
                        .where(paymentStats.popupId.eq(popupId))
                        .fetchOne();

        Double todayAvg =
                queryFactory
                        .select(avgExpression)
                        .from(paymentStats)
                        .where(
                                paymentStats.popupId.eq(popupId),
                                paymentStats.analyzedDate.eq(today))
                        .fetchOne();

        return new PaymentAverageResponse(
                (int) Math.round(totalAvg != null ? totalAvg : 0),
                (int) Math.round(todayAvg != null ? todayAvg : 0));
    }
}
