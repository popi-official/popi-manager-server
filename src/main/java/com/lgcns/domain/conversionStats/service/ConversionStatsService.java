package com.lgcns.domain.conversionStats.service;

import com.lgcns.domain.conversionStats.dto.response.ConversionItemsResponse;

public interface ConversionStatsService {
    ConversionItemsResponse findConversionItems(Long popupId);

    void createConversionStats();
}
