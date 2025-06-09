package com.lgcns.domain.congestionStats.service;

import com.lgcns.domain.congestionStats.domain.CongestionStats;
import com.lgcns.domain.congestionStats.dto.response.CongestionStatsResponse;
import java.util.List;

public interface CongestionStatsService {

    CongestionStatsResponse getCongestionStats(Long popupId);

    List<Long> findTargetPopupIds();

    CongestionStats convertCongestionStats(Long popupId);
}
