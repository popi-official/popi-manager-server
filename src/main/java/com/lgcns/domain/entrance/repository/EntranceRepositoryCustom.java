package com.lgcns.domain.entrance.repository;

import com.lgcns.domain.entrance.dto.response.DailyEntrantCountResponse;
import com.lgcns.domain.entrance.dto.response.HourlyEntranceResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface EntranceRepositoryCustom {
    DailyEntrantCountResponse findDailyEntrantCount(Long popupId, LocalDate today);

    Optional<HourlyEntranceResponse> findHourlyEntrance(
            Long popupId, LocalDate nowDate, LocalTime nowTime);

    List<Long> findPopupIdsWithEntrances(List<Long> popupIds);
}
