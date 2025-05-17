package com.lgcns.domain.congestionStats.service;

import com.lgcns.domain.congestionStats.dto.response.CongestionStatsResponse;

public interface CongestionStatsService {

    CongestionStatsResponse getCongestionStats(Long popupId);
}
