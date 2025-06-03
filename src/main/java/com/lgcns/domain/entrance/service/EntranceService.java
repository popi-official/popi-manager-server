package com.lgcns.domain.entrance.service;

import com.lgcns.domain.entrance.dto.response.DailyEntrantCountResponse;

public interface EntranceService {

    DailyEntrantCountResponse findDailyEntrantCount(Long popupId);
}
