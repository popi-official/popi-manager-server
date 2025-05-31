package com.lgcns.domain.entrance.repository;

import static com.lgcns.domain.entrance.domain.QEntrance.entrance;

import com.lgcns.domain.entrance.domain.MemberAge;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    public Optional<HourlyEntranceResponse> findHourlyEntrance(
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
                new CaseBuilder()
                        .when(entrance.age.eq(MemberAge.TEENAGER))
                        .then(1)
                        .otherwise(0)
                        .sum();

        NumberExpression<Integer> twentyCount =
                new CaseBuilder()
                        .when(entrance.age.eq(MemberAge.TWENTIES))
                        .then(1)
                        .otherwise(0)
                        .sum();

        NumberExpression<Integer> thirtyCount =
                new CaseBuilder()
                        .when(entrance.age.eq(MemberAge.THIRTIES))
                        .then(1)
                        .otherwise(0)
                        .sum();

        NumberExpression<Integer> fortyCount =
                new CaseBuilder()
                        .when(entrance.age.eq(MemberAge.FORTIES_AND_ABOVE))
                        .then(1)
                        .otherwise(0)
                        .sum();

        return Optional.ofNullable(
                queryFactory
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
                        .fetchOne());
    }

    @Override
    public Set<Long> findPopupIdsWithEntrances(List<Long> popupIds) {
        return new HashSet<>(
                queryFactory
                        .select(entrance.popupId)
                        .distinct()
                        .from(entrance)
                        .where(entrance.popupId.in(popupIds))
                        .fetch());
    }

    private LocalTime getPreviousHour(LocalTime nowTime) {
        return nowTime.minusHours(1).truncatedTo(ChronoUnit.HOURS);
    }
}
