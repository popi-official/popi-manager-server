package com.lgcns.domain.item.service;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.dto.request.ItemCreateRequest;
import com.lgcns.domain.item.repository.ItemRepository;
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

    @Override
    public void createItem(ItemCreateRequest request) {

        Item item =
                Item.createItem(
                        request.name(),
                        request.price(),
                        request.imageUrl(),
                        request.qty(),
                        request.minQty(),
                        request.location());

        itemRepository.save(item);
    }

    @Override
    public void createItemByExcel(MultipartFile itemFile)
            throws InvalidFormatException, IOException {

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
            String price = String.valueOf((int) row.getCell(1).getNumericCellValue());
            String imageUrl = row.getCell(2).getStringCellValue();
            int qty = (int) row.getCell(3).getNumericCellValue();
            int minQty = (int) row.getCell(4).getNumericCellValue();
            String location = row.getCell(5).getStringCellValue();

            items.add(Item.createItem(name, price, imageUrl, qty, minQty, location));
        }

        itemRepository.saveAll(items);

        workbook.close();
    }
}
