package com.lgcns.domain.conversionStats.repository;

import com.lgcns.domain.conversionStats.domain.ConversionStats;
import com.lgcns.domain.conversionStats.dto.response.ConversionItem;
import java.util.List;

public interface ConversionStatsRepositoryCustom {
    List<ConversionItem> findTop6LowConversionItemsByPopupId(Long popupId);

    List<ConversionItem> findTop6HighConversionItemsByPopupId(Long popupId);

    void bulkInsertConversionStats(List<ConversionStats> statsList);
}
