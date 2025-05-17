package com.lgcns.domain.congestionStats.repository;

import static com.lgcns.domain.congestionStats.domain.QCongestionStats.congestionStats;

import com.lgcns.domain.congestionStats.dto.response.CongestionStatsResponse;
import com.lgcns.domain.congestionStats.dto.response.DailyCongestionStatsResponse;
import com.lgcns.domain.reservationStats.dto.response.DayOfWeek;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CongestionStatsRepositoryImpl implements CongestionStatsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public CongestionStatsResponse findDailyCongestionStats(
            Long popupId, LocalTime startTime, LocalTime endTime) {

        List<Integer> ALL_HOURS = generateHourlyTimeList(startTime, endTime);
        List<DayOfWeek> ALL_DAYS = Arrays.asList(DayOfWeek.values());

        NumberExpression<Integer> dayOfWeekOrder =
                new CaseBuilder()
                        .when(congestionStats.dayOfWeek.eq(DayOfWeek.MONDAY))
                        .then(0)
                        .when(congestionStats.dayOfWeek.eq(DayOfWeek.TUESDAY))
                        .then(1)
                        .when(congestionStats.dayOfWeek.eq(DayOfWeek.WEDNESDAY))
                        .then(2)
                        .when(congestionStats.dayOfWeek.eq(DayOfWeek.THURSDAY))
                        .then(3)
                        .when(congestionStats.dayOfWeek.eq(DayOfWeek.FRIDAY))
                        .then(4)
                        .when(congestionStats.dayOfWeek.eq(DayOfWeek.SATURDAY))
                        .then(5)
                        .when(congestionStats.dayOfWeek.eq(DayOfWeek.SUNDAY))
                        .then(6)
                        .otherwise(7);

        List<DailyCongestionStatsResponse> result =
                queryFactory
                        .select(
                                Projections.constructor(
                                        DailyCongestionStatsResponse.class,
                                        congestionStats.dayOfWeek,
                                        congestionStats.analyzedTime.hour(),
                                        congestionStats.entrantCount.avg().intValue()))
                        .from(congestionStats)
                        .where(congestionStats.popupId.eq(popupId))
                        .groupBy(congestionStats.dayOfWeek, congestionStats.analyzedTime)
                        .orderBy(dayOfWeekOrder.asc(), congestionStats.analyzedTime.asc())
                        .fetch();

        Map<DayOfWeek, Map<Integer, DailyCongestionStatsResponse>> statsMap =
                result.stream()
                        .collect(
                                Collectors.groupingBy(
                                        DailyCongestionStatsResponse::dayOfWeek,
                                        Collectors.toMap(
                                                DailyCongestionStatsResponse::time,
                                                Function.identity())));

        Map<DayOfWeek, List<DailyCongestionStatsResponse>> completed = new LinkedHashMap<>();

        for (DayOfWeek day : ALL_DAYS) {
            Map<Integer, DailyCongestionStatsResponse> timeMap =
                    statsMap.getOrDefault(day, Map.of());
            List<DailyCongestionStatsResponse> fullList = new ArrayList<>();

            for (Integer hour : ALL_HOURS) {
                DailyCongestionStatsResponse data =
                        timeMap.getOrDefault(hour, DailyCongestionStatsResponse.empty(day, hour));
                fullList.add(data);
            }

            completed.put(day, fullList);
        }

        return new CongestionStatsResponse(
                completed.get(DayOfWeek.MONDAY),
                completed.get(DayOfWeek.TUESDAY),
                completed.get(DayOfWeek.WEDNESDAY),
                completed.get(DayOfWeek.THURSDAY),
                completed.get(DayOfWeek.FRIDAY),
                completed.get(DayOfWeek.SATURDAY),
                completed.get(DayOfWeek.SUNDAY));
    }

    private List<Integer> generateHourlyTimeList(LocalTime startTime, LocalTime endTime) {
        List<Integer> timeList = new ArrayList<>();
        LocalTime current = startTime;

        while (!current.isAfter(endTime)) {
            timeList.add(current.getHour());
            current = current.plusHours(2);
        }

        return timeList;
    }
}
