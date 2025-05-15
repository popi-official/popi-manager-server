package com.lgcns.domain.itemAnalysis.repository;

import com.lgcns.domain.itemAnalysis.domain.ItemSalesStats;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemSalesStatsRepository extends JpaRepository<ItemSalesStats, Long> {
    List<ItemSalesStats> findByPopupId(Long popupId);
}
