package com.lgcns.domain.paymentStats.repository;

import static com.lgcns.domain.paymentStats.domain.QPaymentStats.paymentStats;

import com.lgcns.domain.paymentStats.domain.AveragePeriod;
import com.lgcns.domain.paymentStats.domain.PaymentStats;
import com.lgcns.domain.paymentStats.dto.response.AverageAmountResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentStatsRepositoryImpl implements PaymentStatsRepositoryCustom {

    private final EntityManager em;
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

    @Override
    public void bulkInsertPaymentStats(List<PaymentStats> statsList) {
        if (statsList.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        sb.append(
                "INSERT INTO payment_stats (popup_id, average_amount, period, analyzed_date, analyzed_time) VALUES ");

        for (int i = 0; i < statsList.size(); i++) {
            PaymentStats ps = statsList.get(i);
            sb.append("(")
                    .append(ps.getPopupId())
                    .append(", ")
                    .append(ps.getAverageAmount())
                    .append(", ")
                    .append("'")
                    .append(ps.getPeriod().name())
                    .append("', ")
                    .append("'")
                    .append(ps.getAnalyzedDate())
                    .append("', ")
                    .append("'")
                    .append(ps.getAnalyzedTime())
                    .append("'")
                    .append(")");

            if (i < statsList.size() - 1) sb.append(", ");
        }

        em.createNativeQuery(sb.toString()).executeUpdate();
    }
}
