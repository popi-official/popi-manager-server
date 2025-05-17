package com.lgcns.domain.conversionStats.repository;

import com.lgcns.domain.conversionStats.dto.response.ConversionStatsResponse;

public interface ConversionStatsRepositoryCustom {
    ConversionStatsResponse findTopAndLowConversionItems(Long popupId);
}
