package com.lgcns.domain.conversionStats.repository;

import com.lgcns.domain.conversionStats.domain.ConversionStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversionStatsRepository
        extends JpaRepository<ConversionStats, Long>, ConversionStatsRepositoryCustom {}
