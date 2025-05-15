package com.lgcns.domain.entrant.service;

import com.lgcns.domain.entrant.dto.response.DailyEntrantCountResponse;

public interface DailyEntrantCountService {

    DailyEntrantCountResponse findDailyEntrantCount(Long popupId);
}
