package com.lgcns.domain.paymentStats.repository;

import static com.lgcns.domain.paymentStats.domain.QPaymentStats.paymentStats;

import com.lgcns.domain.paymentStats.domain.AveragePeriod;
import com.lgcns.domain.paymentStats.dto.response.AverageAmountResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentStatsRepositoryImpl implements PaymentStatsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public AverageAmountResponse findLatestAverageAmountByPopupId(Long popupId) {
        Integer totalAverageAmount =
                queryFactory
                        .select(paymentStats.averageAmount)
                        .from(paymentStats)
                        .where(
                                paymentStats.popupId.eq(popupId),
                                paymentStats.period.eq(AveragePeriod.TOTAL))
                        .orderBy(paymentStats.analyzedTime.desc())
                        .fetchFirst();

        Integer todayAverageAmount =
                queryFactory
                        .select(paymentStats.averageAmount)
                        .from(paymentStats)
                        .where(
                                paymentStats.popupId.eq(popupId),
                                paymentStats.period.eq(AveragePeriod.TODAY),
                                paymentStats.analyzedDate.eq(LocalDate.now()))
                        .orderBy(paymentStats.analyzedTime.desc())
                        .fetchFirst();

        totalAverageAmount = totalAverageAmount != null ? totalAverageAmount : 0;
        todayAverageAmount = todayAverageAmount != null ? todayAverageAmount : 0;

        return AverageAmountResponse.of(totalAverageAmount, todayAverageAmount);
    }
}
