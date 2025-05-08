package com.lgcns.domain.item.service;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.dto.request.ItemCreateRequest;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public void createItem(ItemCreateRequest request) {
        Popup popup = findPopupById(request.popupId());

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
    public void createItemByExcel(MultipartFile itemFile)
            throws InvalidFormatException, IOException {

        Popup popup = findPopupById()

        // 업로드된 엑셀 파일을 읽어오기 위해 OPCPackage로 open
        OPCPackage opcPackage = OPCPackage.open(itemFile.getInputStream());

        // XSSFWorkbook 객체로 변환 (xlsx 전용)
        XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);

        // 첫 번째 sheet 읽기
        String sheetName = workbook.getSheetName(0);
        Sheet sheet = workbook.getSheet(sheetName);

        int rows = sheet.getPhysicalNumberOfRows();
        List<Item> items = new ArrayList<>();

        // row 0은 보통 header -> row 1부터 읽기
        for (int rowIndex = 1; rowIndex < rows; rowIndex++) {
            Row row = sheet.getRow(rowIndex);

            String name = row.getCell(0).getStringCellValue();
            String imageUrl = row.getCell(2).getStringCellValue();
            int price = (int) row.getCell(1).getNumericCellValue();
            int stock = (int) row.getCell(3).getNumericCellValue();
            int minStock = (int) row.getCell(4).getNumericCellValue();
            String location = row.getCell(5).getStringCellValue();

            items.add(Item.createItem(name, imageUrl, price, stock, minStock, location));
        }

        itemRepository.saveAll(items);

        workbook.close();
    }

    private Popup findPopupById(Long popupId){
        return popupRepository
                .findById(popupId)
                .orElseThrow(() -> new CustomException(PopupErrorCode.POPUP_NOT_FOUND));
    }
}
