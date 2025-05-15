package com.lgcns.domain.visitorStats.repository;

import static com.lgcns.domain.visitorStats.domain.QVisitorStats.visitorStats;

import com.lgcns.domain.visitorStats.dto.response.VisitorStatsResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
}
