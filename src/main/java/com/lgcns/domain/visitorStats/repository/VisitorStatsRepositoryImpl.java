package com.lgcns.domain.visitorStats.repository;

import static com.lgcns.domain.visitorStats.domain.QVisitorStats.visitorStats;

import com.lgcns.domain.visitorStats.domain.VisitorStats;
import com.lgcns.domain.visitorStats.dto.response.VisitorStatsResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class VisitorStatsRepositoryImpl implements VisitorStatsRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public VisitorStatsResponse getVisitorStatsByPopupId(Long popupId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                VisitorStatsResponse.class,
                                visitorStats.maleCount.sum(),
                                visitorStats.femaleCount.sum(),
                                visitorStats.teenCount.sum(),
                                visitorStats.twentyCount.sum(),
                                visitorStats.thirtyCount.sum(),
                                visitorStats.fortyCount.sum()))
                .from(visitorStats)
                .where(visitorStats.popupId.eq(popupId))
                .fetchOne();
    }

    @Override
    public List<Long> findPopupIdsWithoutVisitorStats(
            List<Long> popupIds, LocalDate nowDate, LocalTime nowTime) {
        List<Long> existingIds =
                queryFactory
                        .select(visitorStats.popupId)
                        .distinct()
                        .from(visitorStats)
                        .where(
                                visitorStats.popupId.in(popupIds),
                                visitorStats.analyzedDate.eq(nowDate),
                                visitorStats.analyzedTime.hour().eq(nowTime.getHour()))
                        .fetch();

        return popupIds.stream()
                .filter(id -> !existingIds.contains(id))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void bulkInsertVisitorStats(List<VisitorStats> visitorStatsList) {
        if (visitorStatsList.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO visitor_stats ")
                .append(
                        "(popup_id, male_count, female_count, teen_count, twenty_count, thirty_count, forty_count, analyzed_date, analyzed_time) VALUES ");

        for (int i = 0; i < visitorStatsList.size(); i++) {
            VisitorStats v = visitorStatsList.get(i);

            sb.append("(")
                    .append(v.getPopupId())
                    .append(", ")
                    .append(v.getMaleCount())
                    .append(", ")
                    .append(v.getFemaleCount())
                    .append(", ")
                    .append(v.getTeenCount())
                    .append(", ")
                    .append(v.getTwentyCount())
                    .append(", ")
                    .append(v.getThirtyCount())
                    .append(", ")
                    .append(v.getFortyCount())
                    .append(", ")
                    .append("'")
                    .append(v.getAnalyzedDate())
                    .append("', ")
                    .append("'")
                    .append(v.getAnalyzedTime())
                    .append("')");

            if (i < visitorStatsList.size() - 1) {
                sb.append(", ");
            }
        }

        em.createNativeQuery(sb.toString()).executeUpdate();
    }
}
