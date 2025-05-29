package com.lgcns.domain.entrance.service;

import com.lgcns.domain.entrance.dto.request.EntranceCreateRequest;
import com.lgcns.domain.entrance.dto.response.DailyEntrantCountResponse;

public interface EntranceService {
    void createEntrance(EntranceCreateRequest request);

    DailyEntrantCountResponse findDailyEntrantCount(Long popupId);
}
