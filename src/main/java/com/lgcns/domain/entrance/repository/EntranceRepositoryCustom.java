package com.lgcns.domain.entrance.repository;

import com.lgcns.domain.entrance.dto.response.DailyEntrantCountResponse;
import com.lgcns.domain.entrance.dto.response.HourlyEntranceResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface EntranceRepositoryCustom {
    DailyEntrantCountResponse findDailyEntrantCount(Long popupId, LocalDate today);

    List<HourlyEntranceResponse> findHourlyEntrances(Long popupId, LocalDate today, LocalTime now);
}
