package com.lgcns.domain.entrance.repository;

import com.lgcns.domain.entrance.dto.response.DailyEntrantCountResponse;
import java.time.LocalDate;

public interface EntranceRepositoryCustom {
    DailyEntrantCountResponse findDailyEntrantCount(Long popupId, LocalDate today);
}
