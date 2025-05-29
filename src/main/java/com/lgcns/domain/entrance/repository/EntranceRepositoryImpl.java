package com.lgcns.domain.entrance.repository;

import static com.lgcns.domain.entrance.domain.QEntrance.entrance;

import com.lgcns.domain.entrance.domain.MemberGender;
import com.lgcns.domain.entrance.dto.response.DailyEntrantCountResponse;
import com.lgcns.domain.entrance.dto.response.HourlyEntranceResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
    public HourlyEntranceResponse findHourlyEntrances(
            Long popupId, LocalDate nowDate, LocalTime nowTime) {
        NumberExpression<Integer> maleCount =
                new CaseBuilder()
                        .when(entrance.gender.eq(MemberGender.MALE))
                        .then(1)
                        .otherwise(0)
                        .sum();

        NumberExpression<Integer> femaleCount =
                new CaseBuilder()
                        .when(entrance.gender.eq(MemberGender.FEMALE))
                        .then(1)
                        .otherwise(0)
                        .sum();

        NumberExpression<Integer> teenCount =
                new CaseBuilder().when(entrance.ageGroup.eq(10)).then(1).otherwise(0).sum();

        NumberExpression<Integer> twentyCount =
                new CaseBuilder().when(entrance.ageGroup.eq(20)).then(1).otherwise(0).sum();

        NumberExpression<Integer> thirtyCount =
                new CaseBuilder().when(entrance.ageGroup.eq(30)).then(1).otherwise(0).sum();

        NumberExpression<Integer> fortyCount =
                new CaseBuilder().when(entrance.ageGroup.eq(40)).then(1).otherwise(0).sum();

        return queryFactory
                .select(
                        Projections.constructor(
                                HourlyEntranceResponse.class,
                                maleCount,
                                femaleCount,
                                teenCount,
                                twentyCount,
                                thirtyCount,
                                fortyCount,
                                entrance.reservationDate,
                                entrance.reservationTime))
                .from(entrance)
                .where(
                        entrance.popupId.eq(popupId),
                        entrance.reservationDate.eq(nowDate),
                        entrance.reservationTime.eq(getPreviousHour(nowTime)))
                .groupBy(entrance.reservationDate, entrance.reservationTime)
                .fetchOne();
    }

    private LocalTime getPreviousHour(LocalTime nowTime) {
        return nowTime.minusHours(1).truncatedTo(ChronoUnit.HOURS);
    }
}
