package com.lgcns.domain.item.service;

import com.lgcns.domain.item.client.dto.request.ItemIdsForPaymentRequest;
import com.lgcns.domain.item.client.dto.response.ItemForPaymentResponse;
import com.lgcns.domain.item.client.dto.response.ItemInfoResponse;
import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.dto.request.ItemCreateRequest;
import com.lgcns.domain.item.dto.request.ItemMinStockUpdateRequest;
import com.lgcns.domain.item.dto.response.ItemDetailResponse;
import com.lgcns.domain.item.dto.response.ItemLocationResponse;
import com.lgcns.domain.item.dto.response.ItemPreviewResponse;
import com.lgcns.domain.item.exception.ItemErrorCode;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.common.response.SliceResponse;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final PopupRepository popupRepository;
    private final ManagerUtil managerUtil;

    @Override
    public void createItem(Long popupId, ItemCreateRequest request) {
        final Manager currentManager = managerUtil.getCurrentManager();
        final Popup popup = findPopupById(popupId);

        validatePopupOwnership(currentManager, popup);

        Item item =
                Item.createItem(
                        popup,
                        request.name(),
                        request.imageUrl(),
                        request.price(),
                        request.stock(),
                        request.minStock(),
                        request.location());

        itemRepository.save(item);
    }

    @Override
    public void createItemByExcel(Long popupId, MultipartFile itemFile)
            throws InvalidFormatException, IOException {
        final Manager currentManager = managerUtil.getCurrentManager();
        final Popup popup = findPopupById(popupId);

        validatePopupOwnership(currentManager, popup);

        // try-with-resources
        try (InputStream inputStream = itemFile.getInputStream();
                OPCPackage opcPackage = OPCPackage.open(inputStream);
                XSSFWorkbook workbook = new XSSFWorkbook(opcPackage)) {

            // 첫 번째 sheet 읽기
            String sheetName = workbook.getSheetName(0);
            Sheet sheet = workbook.getSheet(sheetName);

            int rows = sheet.getPhysicalNumberOfRows();
            List<Item> items = new ArrayList<>();

            // row 0은 header
            for (int rowIndex = 0; rowIndex <= rows; rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                if (rowIndex == 0 || row == null) {
                    continue;
                }

                String name = row.getCell(0).getStringCellValue();
                String imageUrl = row.getCell(1).getStringCellValue();
                int price = (int) row.getCell(2).getNumericCellValue();
                int stock = (int) row.getCell(3).getNumericCellValue();
                int minStock = (int) row.getCell(4).getNumericCellValue();
                String location = row.getCell(5).getStringCellValue();

                items.add(Item.createItem(popup, name, imageUrl, price, stock, minStock, location));
            }

            itemRepository.saveAll(items);
        }
    }

    @Override
    public Map<String, List<ItemPreviewResponse>> findAllItems(Long popupId) {
        List<ItemLocationResponse> projections = itemRepository.findItemsWithSplitLocation(popupId);

        return groupItemsByLocation(projections);
    }

    @Override
    public ItemDetailResponse updateItemMinStock(
            Long popupId, Long itemId, ItemMinStockUpdateRequest request) {
        final Manager currentManager = managerUtil.getCurrentManager();
        final Popup popup = findPopupById(popupId);
        validatePopupOwnership(currentManager, popup);

        final Item item = findByItemId(itemId);
        validateItemBelongsToPopup(item, popupId);

        validateMinStock(item, request.minStock());

        item.updateMinStock(request.minStock());

        return ItemDetailResponse.from(item);
    }

    @Override
    public void deleteItem(Long popupId, Long itemId) {
        final Manager currentManager = managerUtil.getCurrentManager();

        final Popup popup = findPopupById(popupId);
        validatePopupOwnership(currentManager, popup);

        final Item item = findByItemId(itemId);
        validateItemBelongsToPopup(item, popupId);

        itemRepository.delete(item);
    }

    @Override
    @Transactional(readOnly = true)
    public SliceResponse<ItemInfoResponse> findItemsByNameWithPagination(
            Long popupId, String keyword, Long lastItemId, int size) {
        Slice<ItemInfoResponse> itemInfoResponses =
                itemRepository.findItemsByNameWithPagination(popupId, keyword, lastItemId, size);

        return SliceResponse.from(itemInfoResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemInfoResponse> findRandomItems(Long popupId) {
        return itemRepository.findRandomItems(popupId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemForPaymentResponse> findItemsForPayment(
            Long popupId, ItemIdsForPaymentRequest request) {
        return itemRepository.findItemsForPayment(popupId, request.itemIds());
    }

    @Override
    public void updateItemAverageSales() {
        itemRepository.bulkUpdateAverageSalesForOperatingPopups();
    }

    @Override
    public void calculateItemAverageSales() {
        itemRepository.bulkUpdateAverageSalesForOperatingPopups();
    }

    private Popup findPopupById(Long popupId) {
        return popupRepository
                .findById(popupId)
                .orElseThrow(() -> new CustomException(PopupErrorCode.POPUP_NOT_FOUND));
    }

    private Map<String, List<ItemPreviewResponse>> groupItemsByLocation(
            List<ItemLocationResponse> projections) {
        return projections.stream()
                .collect(
                        Collectors.groupingBy(
                                ItemLocationResponse::locationGroup,
                                Collectors.mapping(
                                        ItemLocationResponse::toPreviewResponse,
                                        Collectors.toList())));
    }

    private void validatePopupOwnership(Manager manager, Popup popup) {
        if (!popup.getManager().equals(manager)) {
            throw new CustomException(PopupErrorCode.POPUP_UNAUTHORIZED);
        }
    }

    private void validateItemBelongsToPopup(Item item, Long popupId) {
        if (!item.getPopup().getId().equals(popupId)) {
            throw new CustomException(ItemErrorCode.ITEM_POPUP_MISMATCH);
        }
    }

    private Item findByItemId(Long itemId) {
        return itemRepository
                .findById(itemId)
                .orElseThrow(() -> new CustomException(ItemErrorCode.ITEM_NOT_FOUND));
    }

    private void validateMinStock(Item item, int minStock) {
        if (minStock > item.getStock()) {
            throw new CustomException(ItemErrorCode.MIN_STOCK_EXCEEDED);
        }
    }
}
