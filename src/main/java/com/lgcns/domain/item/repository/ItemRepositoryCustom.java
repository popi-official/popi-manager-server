package com.lgcns.domain.item.repository;

import com.lgcns.domain.item.client.dto.ItemInfoResponse;
import com.lgcns.domain.item.dto.response.ItemLocationResponse;
import java.util.List;
import org.springframework.data.domain.Slice;

public interface ItemRepositoryCustom {
    List<ItemLocationResponse> findItemsWithSplitLocation(Long popupId);

    Slice<ItemInfoResponse> findItemsByNameWithPagination(
            Long popupId, String keyword, Long lastItemId, int size);

    List<ItemInfoResponse> findRandomItems(Long popupId);
}
