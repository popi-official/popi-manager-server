package com.lgcns.domain.itemAnalysis.repository;

import com.lgcns.domain.itemAnalysis.domain.ItemAnalysis;
import java.util.List;

public interface ItemAnalysisRepositoryCustom {
    List<ItemAnalysis> findTop3ItemsByPopupId(Long popupId);
}
