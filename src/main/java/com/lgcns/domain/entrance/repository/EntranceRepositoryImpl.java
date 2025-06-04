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
import java.util.List;
import java.util.Optional;
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
        NumberExpression<Integer> maleCount = createGenderCountCase(MemberGender.MALE);
        NumberExpression<Integer> femaleCount = createGenderCountCase(MemberGender.FEMALE);

        NumberExpression<Integer> teenCount = createAgeCountCase(MemberAge.TEENAGER);
        NumberExpression<Integer> twentyCount = createAgeCountCase(MemberAge.TWENTIES);
        NumberExpression<Integer> thirtyCount = createAgeCountCase(MemberAge.THIRTIES);
        NumberExpression<Integer> fortyCount = createAgeCountCase(MemberAge.FORTIES_AND_ABOVE);

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
    public List<Long> findPopupIdsWithEntrances(List<Long> popupIds) {
        return queryFactory
                .select(entrance.popupId)
                .distinct()
                .from(entrance)
                .where(entrance.popupId.in(popupIds))
                .fetch();
    }

    private NumberExpression<Integer> createGenderCountCase(MemberGender gender) {
        return new CaseBuilder().when(entrance.gender.eq(gender)).then(1).otherwise(0).sum();
    }

    private NumberExpression<Integer> createAgeCountCase(MemberAge age) {
        return new CaseBuilder().when(entrance.age.eq(age)).then(1).otherwise(0).sum();
    }

    private LocalTime getPreviousHour(LocalTime nowTime) {
        return nowTime.minusHours(1).truncatedTo(ChronoUnit.HOURS);
    }
}
