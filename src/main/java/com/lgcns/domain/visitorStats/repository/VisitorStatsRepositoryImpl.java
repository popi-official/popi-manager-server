package com.lgcns.domain.visitorStats.repository;

import static com.lgcns.domain.visitorStats.domain.QVisitorStats.visitorStats;

import com.lgcns.domain.visitorStats.dto.response.VisitorStatsResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VisitorStatsRepositoryImpl implements VisitorStatsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

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
    public Set<Long> findPopupIdsWithoutVisitorStats(
            Set<Long> popupIds, LocalDate nowDate, LocalTime nowTime) {
        Set<Long> existingIds =
                new HashSet<>(
                        queryFactory
                                .select(visitorStats.popupId)
                                .from(visitorStats)
                                .where(
                                        visitorStats.popupId.in(popupIds),
                                        visitorStats.analyzedDate.eq(nowDate),
                                        visitorStats.analyzedTime.hour().eq(nowTime.getHour()))
                                .fetch());

        return popupIds.stream()
                .filter(id -> !existingIds.contains(id))
                .collect(Collectors.toSet());
    }
}
