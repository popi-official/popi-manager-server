package com.lgcns.domain.entrance.repository;

import com.lgcns.domain.entrance.dto.response.DailyEntrantCountResponse;
import com.lgcns.domain.entrance.dto.response.HourlyEntranceResponse;
import java.time.LocalDate;
import java.time.LocalTime;

public interface EntranceRepositoryCustom {
    DailyEntrantCountResponse findDailyEntrantCount(Long popupId, LocalDate today);

    HourlyEntranceResponse findHourlyEntrances(Long popupId, LocalDate nowDate, LocalTime nowTime);
}
