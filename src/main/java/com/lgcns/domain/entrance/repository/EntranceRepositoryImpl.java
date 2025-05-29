package com.lgcns.domain.entrance.repository;

import static com.lgcns.domain.entrance.domain.QEntrance.entrance;

import com.lgcns.domain.entrance.dto.response.DailyEntrantCountResponse;
import com.lgcns.domain.entrance.dto.response.HourlyEntranceResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
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

    @Override
    public List<HourlyEntranceResponse> findHourlyEntrances(
            Long popupId, LocalDate today, LocalTime now) {
        return queryFactory
                .select(
                        Projections.constructor(
                                HourlyEntranceResponse.class,
                                entrance.gender,
                                entrance.ageGroup,
                                entrance.reservationTime,
                                entrance.reservationTime))
                .from(entrance)
                .where(
                        entrance.popupId.eq(popupId),
                        entrance.reservationDate.eq(today),
                        entrance.reservationTime.eq(getPreviousHour(now)))
                .fetch();
    }

    private LocalTime getPreviousHour(LocalTime now) {
        return now.minusHours(1).truncatedTo(ChronoUnit.HOURS);
    }
}
