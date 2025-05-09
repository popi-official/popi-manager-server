package com.lgcns.domain.item.service;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.dto.request.ItemCreateRequest;
import com.lgcns.domain.item.dto.response.ItemPreviewResponse;
import com.lgcns.domain.item.exception.ItemErrorCode;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import jakarta.transaction.Transactional;
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
import org.springframework.stereotype.Service;
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
        Popup popup = findPopupById(popupId);

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

        Popup popup = findPopupById(popupId);

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

    private Popup findPopupById(Long popupId) {
        return popupRepository
                .findById(popupId)
                .orElseThrow(() -> new CustomException(PopupErrorCode.POPUP_NOT_FOUND));
    }

    @Override
    public Map<String, List<ItemPreviewResponse>> findAllItems(Long popupId) {
        List<Item> items = itemRepository.findAllItemsByPopupId(popupId);

        if (items.isEmpty()) {
            throw new CustomException(ItemErrorCode.EMPTY_ITEM_LIST);
        }

        return processAndGroupItems(items);
    }

    private Map<String, List<ItemPreviewResponse>> processAndGroupItems(List<Item> items) {
        return items.stream()
                .collect(
                        Collectors.groupingBy(
                                item -> String.valueOf(item.getLocation().charAt(0)),
                                Collectors.mapping(
                                        item -> mapToItemPreviewResponse(item),
                                        Collectors.toList())));
    }

    private ItemPreviewResponse mapToItemPreviewResponse(Item item) {
        return ItemPreviewResponse.of(
                item.getLocation().substring(1),
                item.getId(),
                item.getName(),
                item.getImageUrl(),
                item.getPrice(),
                item.getStock());
    }

    @Override
    public void deleteItem(Long itemId) {
        Item item =
                itemRepository
                        .findById(itemId)
                        .orElseThrow(() -> new CustomException(ItemErrorCode.ITEM_NOT_FOUND));

        validateItemOwnership(item);

        itemRepository.delete(item);
    }

    private void validateItemOwnership(Item item) {
        Long currentManagerId = managerUtil.getCurrentManagerId();

        if (!item.getPopup().getManager().getId().equals(currentManagerId)) {
            throw new CustomException(ItemErrorCode.ITEM_DELETE_UNAUTHORIZED);
        }
    }
}
