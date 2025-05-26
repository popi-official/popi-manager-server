package com.lgcns.domain.item.service;

import com.lgcns.domain.item.client.dto.ItemInfoResponse;
import com.lgcns.domain.item.dto.request.ItemCreateRequest;
import com.lgcns.domain.item.dto.request.ItemMinStockUpdateRequest;
import com.lgcns.domain.item.dto.response.ItemDetailResponse;
import com.lgcns.domain.item.dto.response.ItemPreviewResponse;
import com.lgcns.global.common.response.SliceResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.multipart.MultipartFile;

public interface ItemService {

    void createItem(Long popupId, ItemCreateRequest request);

    void createItemByExcel(Long popupId, MultipartFile itemFile)
            throws InvalidFormatException, IOException;

    Map<String, List<ItemPreviewResponse>> findAllItems(Long popupId);

    void deleteItem(Long popupId, Long itemId);

    ItemDetailResponse updateItemMinStock(
            Long popupId, Long itemId, ItemMinStockUpdateRequest request);

    SliceResponse<ItemInfoResponse> findItemsByNameWithPagination(
            Long popupId, String keyword, Long lastItemId, int size);

    List<ItemInfoResponse> findRandomItems(Long popupId);
}
