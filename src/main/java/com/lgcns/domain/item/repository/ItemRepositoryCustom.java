package com.lgcns.domain.item.repository;

import com.lgcns.domain.item.dto.ItemLocationProjection;
import java.util.List;

public interface ItemRepositoryCustom {
    List<ItemLocationProjection> findItemsWithSplitLocation(Long popupId);
}
