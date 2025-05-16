package com.lgcns.domain.itemAnalysis.repository;

import com.lgcns.domain.itemAnalysis.domain.ItemAnalysis;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemAnalysisRepository
        extends JpaRepository<ItemAnalysis, Long>, ItemAnalysisRepositoryCustom {
    Optional<ItemAnalysis> findByItemId(Long itemId);
}
