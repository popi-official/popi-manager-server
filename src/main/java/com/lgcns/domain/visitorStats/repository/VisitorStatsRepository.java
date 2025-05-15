package com.lgcns.domain.visitorStats.repository;

import com.lgcns.domain.visitorStats.domain.VisitorStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorStatsRepository
        extends JpaRepository<VisitorStats, Long>, VisitorStatsRepositoryCustom {}
