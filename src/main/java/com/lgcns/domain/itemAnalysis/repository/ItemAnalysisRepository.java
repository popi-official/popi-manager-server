package com.lgcns.domain.itemAnalysis.repository;

import com.lgcns.domain.itemAnalysis.domain.ItemAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemAnalysisRepository
        extends JpaRepository<ItemAnalysis, Long>, ItemAnalysisRepositoryCustom {}
