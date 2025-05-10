package com.lgcns.domain.item.repository;

import com.lgcns.domain.item.dto.response.ItemLocationResponse;
import java.util.List;

public interface ItemRepositoryCustom {
    List<ItemLocationResponse> findItemsWithSplitLocation(Long popupId);
}
