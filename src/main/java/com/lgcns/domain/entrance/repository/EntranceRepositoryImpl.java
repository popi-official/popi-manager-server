package com.lgcns.domain.entrance.repository;

import static com.lgcns.domain.entrance.domain.QEntrance.entrance;

import com.lgcns.domain.entrance.dto.response.DailyEntrantCountResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EntranceRepositoryImpl implements EntranceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public DailyEntrantCountResponse findDailyEntrantCount(Long popupId, LocalDate today) {
        return queryFactory
                .select(Projections.constructor(DailyEntrantCountResponse.class, entrance.count()))
                .from(entrance)
                .where(entrance.popupId.eq(popupId), entrance.reservationDate.eq(today))
                .fetchOne();
    }
}
