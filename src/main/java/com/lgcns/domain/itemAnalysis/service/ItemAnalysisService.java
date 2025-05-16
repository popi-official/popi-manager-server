package com.lgcns.domain.itemAnalysis.service;

import com.lgcns.domain.itemAnalysis.dto.response.ItemTrendingResponse;
import java.util.List;

public interface ItemAnalysisService {
    List<ItemTrendingResponse> getTrendingItems(Long popupId);
}
