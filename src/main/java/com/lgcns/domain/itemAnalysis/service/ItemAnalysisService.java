package com.lgcns.domain.itemAnalysis.service;

import com.lgcns.domain.itemAnalysis.domain.ItemAnalysis;
import com.lgcns.domain.itemAnalysis.dto.response.ItemTrendingResponse;
import java.util.List;

public interface ItemAnalysisService {
    List<ItemTrendingResponse> getTrendingItems(Long popupId);

    List<Long> findTargetPopupIds();

    List<ItemAnalysis> processPopupItemAnalysis(Long popupId);

    void saveItemAnalysisList(List<ItemAnalysis> itemAnalysisList);
}
