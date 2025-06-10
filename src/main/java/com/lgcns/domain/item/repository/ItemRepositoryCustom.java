package com.lgcns.domain.item.repository;

import com.lgcns.domain.item.client.dto.response.ItemForPaymentResponse;
import com.lgcns.domain.item.client.dto.response.ItemInfoResponse;
import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.dto.response.ItemLocationResponse;
import java.util.List;
import org.springframework.data.domain.Slice;

public interface ItemRepositoryCustom {
    List<ItemLocationResponse> findItemsWithSplitLocation(Long popupId);

    Slice<ItemInfoResponse> findItemsByNameWithPagination(
            Long popupId, String keyword, Long lastItemId, int size);

    List<ItemInfoResponse> findRandomItems(Long popupId);

    List<ItemForPaymentResponse> findItemsForPayment(Long popupId, List<Long> itemIds);

    List<Item> findTopItemsByPopupId(Long popupId, int limit);

    List<Item> findAllByPopupId(Long popupId);

    void bulkUpdate(List<Item> itemList);
}
