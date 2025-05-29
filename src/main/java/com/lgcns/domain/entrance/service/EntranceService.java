package com.lgcns.domain.entrance.service;

import com.lgcns.domain.entrance.dto.request.EntranceCreateRequest;
import com.lgcns.domain.entrance.dto.response.DailyEntrantCountResponse;
import com.lgcns.domain.entrance.dto.response.HourlyEntranceResponse;
import java.util.List;

public interface EntranceService {
    void createEntrance(EntranceCreateRequest request);

    DailyEntrantCountResponse findDailyEntrantCount(Long popupId);

    List<HourlyEntranceResponse> findHourlyEntrances(Long popupId);
}
