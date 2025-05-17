package com.lgcns.domain.congestionStats.repository;

import com.lgcns.domain.congestionStats.domain.CongestionStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CongestionStatsRepository
        extends JpaRepository<CongestionStats, Integer>, CongestionStatsRepositoryCustom {}
