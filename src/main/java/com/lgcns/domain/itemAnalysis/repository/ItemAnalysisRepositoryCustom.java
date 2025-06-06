package com.lgcns.domain.itemAnalysis.repository;

import com.lgcns.domain.itemAnalysis.domain.ItemAnalysis;
import java.util.List;

public interface ItemAnalysisRepositoryCustom {
    List<ItemAnalysis> findTopItemsByPopupId(Long popupId, int limit);

    List<ItemAnalysis> findAllByPopupId(Long popupId);

    void bulkInsertOrUpdate(List<ItemAnalysis> itemAnalysisList);
}
