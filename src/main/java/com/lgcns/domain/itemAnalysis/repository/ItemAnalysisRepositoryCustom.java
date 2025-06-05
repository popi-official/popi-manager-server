package com.lgcns.domain.itemAnalysis.repository;

import com.lgcns.domain.itemAnalysis.domain.ItemAnalysis;
import com.lgcns.domain.itemAnalysis.dto.response.ItemTrendingResponse;
import java.util.List;

public interface ItemAnalysisRepositoryCustom {
    List<ItemTrendingResponse> findTopItemsByPopupId(Long popupId, int limit);

    List<ItemAnalysis> findAllByPopupId(Long popupId);

    void bulkInsertOrUpdate(List<ItemAnalysis> itemAnalysisList);
}
