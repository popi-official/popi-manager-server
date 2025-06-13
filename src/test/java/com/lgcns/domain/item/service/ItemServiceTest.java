package com.lgcns.domain.item.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.item.client.dto.request.ItemIdsForPaymentRequest;
import com.lgcns.domain.item.client.dto.response.ItemForPaymentResponse;
import com.lgcns.domain.item.client.dto.response.ItemInfoResponse;
import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.dto.request.ItemCreateRequest;
import com.lgcns.domain.item.dto.request.ItemMinStockUpdateRequest;
import com.lgcns.domain.item.dto.response.ItemDetailResponse;
import com.lgcns.domain.item.dto.response.ItemPreviewResponse;
import com.lgcns.domain.item.dto.response.ItemTrendingResponse;
import com.lgcns.domain.item.exception.ItemErrorCode;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.common.response.SliceResponse;
import com.lgcns.global.error.exception.CustomException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class ItemServiceTest extends IntegrationTest {
    @Autowired ItemService itemService;
    @Autowired ItemRepository itemRepository;
    @Autowired PopupRepository popupRepository;
    @Autowired ManagerRepository managerRepository;

    private Manager ownerManager;
    private Manager otherManager;
    private Popup popup;
    private Popup otherPopup;
    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setUp() {
        ownerManager =
                managerRepository.save(Manager.createManager("testManager1", "testPassword"));
        otherManager =
                managerRepository.save(Manager.createManager("otherManager", "testPassword"));

        setAuthentication(ownerManager);

        popup =
                Popup.createPopup(
                        ownerManager,
                        "testPopup",
                        "https://bucket/이미지.jpg",
                        LocalDate.parse("2025-01-01"),
                        LocalDate.parse("2025-01-31"),
                        LocalDateTime.parse("2025-01-01T10:00:00"),
                        LocalDateTime.parse("2025-01-31T20:00:00"),
                        LocalTime.parse("10:00:00"),
                        LocalTime.parse("20:00:00"),
                        100,
                        20,
                        "서울특별시 강남구 테헤란로 123",
                        "3층 A호",
                        37.123456,
                        127.123456);
        popup = popupRepository.save(popup);

        otherPopup =
                Popup.createPopup(
                        ownerManager,
                        "testPopup2",
                        "https://bucket/이미지2.jpg",
                        LocalDate.parse("2025-01-11"),
                        LocalDate.parse("2025-02-20"),
                        LocalDateTime.parse("2025-01-01T10:00:00"),
                        LocalDateTime.parse("2025-01-31T20:00:00"),
                        LocalTime.parse("10:00:00"),
                        LocalTime.parse("20:00:00"),
                        200,
                        30,
                        "인천광역시 서구 비즈니스로 123",
                        "16층 1601호",
                        39.123456,
                        129.123456);
        otherPopup = popupRepository.save(otherPopup);
    }

    @Nested
    class 상품_하나를_등록할_때 {

        @Test
        void 유효한_입력_값이면_상품_등록에_성공한다() {
            // given
            ItemCreateRequest request = createItemCreateRequest();

            // when
            itemService.createItem(popup.getId(), request);

            // then
            Item item = itemRepository.findAll().get(0);
            Assertions.assertAll(
                    () -> assertThat(item.getName()).isEqualTo("테스트 상품"),
                    () -> assertThat(item.getImageUrl()).isEqualTo("https://bucket/item.jpg"),
                    () -> assertThat(item.getPrice()).isEqualTo(10000),
                    () -> assertThat(item.getStock()).isEqualTo(100),
                    () -> assertThat(item.getMinStock()).isEqualTo(10),
                    () -> assertThat(item.getLocation()).isEqualTo("a1"),
                    () -> assertThat(item.getPopup().getId()).isEqualTo(popup.getId()));
        }

        @Test
        void 존재하지_않는_팝업_ID로_상품_등록을_시도하면_실패한다() {
            // given
            final Long popupId = 9999L;
            ItemCreateRequest request = createItemCreateRequest();

            // when & then
            assertThatThrownBy(() -> itemService.createItem(popupId, request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_NOT_FOUND);
        }

        private ItemCreateRequest createItemCreateRequest() {
            return new ItemCreateRequest("테스트 상품", "https://bucket/item.jpg", 10000, 100, 10, "a1");
        }

        @Test
        void 권한이_없는_사용자가_상품을_등록하면_예외가_발생한다() {
            // give
            setAuthentication(otherManager);

            ItemCreateRequest request = createItemCreateRequest();

            // when & then
            assertThatThrownBy(() -> itemService.createItem(popup.getId(), request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_UNAUTHORIZED);
        }
    }

    @Nested
    class 요청받은_엑셀_파일로_상품을_생성할_때 {

        @Test
        void xlsx_파일_업로드에_성공한다() throws IOException {
            // given
            MultipartFile excelFile = createValidExcelFile();

            // when
            itemService.createItemByExcel(popup.getId(), excelFile);

            // then
            List<Item> items = itemRepository.findAll();
            assertThat(items).hasSize(3);

            Assertions.assertAll(
                    () -> assertThat(items.get(0).getName()).isEqualTo("지수 포토카드"),
                    () ->
                            assertThat(items.get(0).getImageUrl())
                                    .isEqualTo("https://bucket/item1.jpg"),
                    () -> assertThat(items.get(0).getPrice()).isEqualTo(5000),
                    () -> assertThat(items.get(0).getStock()).isEqualTo(50),
                    () -> assertThat(items.get(0).getMinStock()).isEqualTo(5),
                    () -> assertThat(items.get(0).getLocation()).isEqualTo("a1"),
                    () -> assertThat(items.get(1).getName()).isEqualTo("제니 포토카드"),
                    () ->
                            assertThat(items.get(1).getImageUrl())
                                    .isEqualTo("https://bucket/item2.jpg"),
                    () -> assertThat(items.get(1).getPrice()).isEqualTo(15000),
                    () -> assertThat(items.get(1).getStock()).isEqualTo(100),
                    () -> assertThat(items.get(1).getMinStock()).isEqualTo(10),
                    () -> assertThat(items.get(1).getLocation()).isEqualTo("a2"),
                    () -> assertThat(items.get(2).getName()).isEqualTo("로제 포토카드"),
                    () ->
                            assertThat(items.get(2).getImageUrl())
                                    .isEqualTo("https://bucket/item3.jpg"),
                    () -> assertThat(items.get(2).getPrice()).isEqualTo(15000),
                    () -> assertThat(items.get(2).getStock()).isEqualTo(100),
                    () -> assertThat(items.get(2).getMinStock()).isEqualTo(10),
                    () -> assertThat(items.get(2).getLocation()).isEqualTo("a3"));
        }

        @Test
        void 파일이_없으면_예외가_발생한다() {
            // given
            MultipartFile nullFile = null;

            // when & then
            assertThatThrownBy(() -> itemService.createItemByExcel(popup.getId(), nullFile))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ItemErrorCode.FILE_NOT_PROVIDED);
        }

        @Test
        void 파일이_비어있으면_예외가_발생한다() {
            // given
            MultipartFile emptyFile =
                    new MockMultipartFile(
                            "itemFile",
                            "empty.xlsx",
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                            new byte[0]);

            // when & then
            assertThatThrownBy(() -> itemService.createItemByExcel(popup.getId(), emptyFile))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ItemErrorCode.FILE_NOT_PROVIDED);
        }

        @Test
        void 파일_크기가_10MB를_초과하면_예외가_발생한다() {
            // given
            byte[] largeContent = new byte[11 * 1024 * 1024];
            MultipartFile largeFile =
                    new MockMultipartFile(
                            "itemFile",
                            "large.xlsx",
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                            largeContent);

            // when & then
            assertThatThrownBy(() -> itemService.createItemByExcel(popup.getId(), largeFile))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ItemErrorCode.FILE_TOO_LARGE);
        }

        @Test
        void 지원하지_않는_파일_형식이면_예외가_발생한다() {
            // given
            MultipartFile txtFile =
                    new MockMultipartFile(
                            "itemFile", "test.txt", "text/plain", "test content".getBytes());

            // when & then
            assertThatThrownBy(() -> itemService.createItemByExcel(popup.getId(), txtFile))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ItemErrorCode.INVALID_FILE_TYPE);
        }

        @Test
        void 엑셀에_데이터가_없으면_예외가_발생한다() throws IOException {
            // given
            MultipartFile emptyDataFile = createExcelFileWithHeaderOnly();

            // when & then
            assertThatThrownBy(() -> itemService.createItemByExcel(popup.getId(), emptyDataFile))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ItemErrorCode.EXCEL_DATA_INVALID);
        }

        @Test
        void 필수_데이터가_누락되면_예외가_발생한다() throws IOException {
            // given
            MultipartFile missingDataFile = createExcelFileWithMissingData();

            // when & then
            assertThatThrownBy(() -> itemService.createItemByExcel(popup.getId(), missingDataFile))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ItemErrorCode.EXCEL_DATA_INVALID);
        }

        @Test
        void 최소재고가_재고보다_크면_예외가_발생한다() throws IOException {
            // given
            MultipartFile invalidStockFile = createExcelFileWithInvalidStock();

            // when & then
            assertThatThrownBy(() -> itemService.createItemByExcel(popup.getId(), invalidStockFile))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ItemErrorCode.EXCEL_DATA_INVALID);
        }

        @Test
        void 음수_값이_있으면_예외가_발생한다() throws IOException {
            // given
            MultipartFile negativeValueFile = createExcelFileWithNegativeValues();

            // when & then
            assertThatThrownBy(
                            () -> itemService.createItemByExcel(popup.getId(), negativeValueFile))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ItemErrorCode.EXCEL_DATA_INVALID);
        }

        @Test
        void 잘못된_데이터_형식이면_예외가_발생한다() throws IOException {
            // given
            MultipartFile invalidFormatFile = createExcelFileWithInvalidDataFormat();

            // when & then
            assertThatThrownBy(
                            () -> itemService.createItemByExcel(popup.getId(), invalidFormatFile))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ItemErrorCode.EXCEL_DATA_INVALID);
        }

        private MultipartFile createExcelFileWithHeaderOnly() throws IOException {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Items");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("상품명");
            headerRow.createCell(1).setCellValue("이미지URL");
            headerRow.createCell(2).setCellValue("가격");
            headerRow.createCell(3).setCellValue("재고");
            headerRow.createCell(4).setCellValue("최소재고");
            headerRow.createCell(5).setCellValue("위치");

            return convertWorkbookToMultipartFile(workbook, "header_only.xlsx");
        }

        private MultipartFile createExcelFileWithMissingData() throws IOException {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Items");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("상품명");
            headerRow.createCell(1).setCellValue("이미지URL");
            headerRow.createCell(2).setCellValue("가격");
            headerRow.createCell(3).setCellValue("재고");
            headerRow.createCell(4).setCellValue("최소재고");
            headerRow.createCell(5).setCellValue("위치");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("");
            dataRow.createCell(1).setCellValue("https://bucket/item1.jpg");
            dataRow.createCell(2).setCellValue(5000);
            dataRow.createCell(3).setCellValue(50);
            dataRow.createCell(4).setCellValue(5);
            dataRow.createCell(5).setCellValue("a1");

            return convertWorkbookToMultipartFile(workbook, "missing_data.xlsx");
        }

        private MultipartFile createExcelFileWithInvalidStock() throws IOException {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Items");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("상품명");
            headerRow.createCell(1).setCellValue("이미지URL");
            headerRow.createCell(2).setCellValue("가격");
            headerRow.createCell(3).setCellValue("재고");
            headerRow.createCell(4).setCellValue("최소재고");
            headerRow.createCell(5).setCellValue("위치");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("테스트 상품");
            dataRow.createCell(1).setCellValue("https://bucket/item1.jpg");
            dataRow.createCell(2).setCellValue(5000);
            dataRow.createCell(3).setCellValue(10);
            dataRow.createCell(4).setCellValue(20);
            dataRow.createCell(5).setCellValue("a1");

            return convertWorkbookToMultipartFile(workbook, "invalid_stock.xlsx");
        }

        private MultipartFile createExcelFileWithNegativeValues() throws IOException {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Items");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("상품명");
            headerRow.createCell(1).setCellValue("이미지URL");
            headerRow.createCell(2).setCellValue("가격");
            headerRow.createCell(3).setCellValue("재고");
            headerRow.createCell(4).setCellValue("최소재고");
            headerRow.createCell(5).setCellValue("위치");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("테스트 상품");
            dataRow.createCell(1).setCellValue("https://bucket/item1.jpg");
            dataRow.createCell(2).setCellValue(-5000);
            dataRow.createCell(3).setCellValue(50);
            dataRow.createCell(4).setCellValue(5);
            dataRow.createCell(5).setCellValue("a1");

            return convertWorkbookToMultipartFile(workbook, "negative_values.xlsx");
        }

        private MultipartFile createExcelFileWithInvalidDataFormat() throws IOException {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Items");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("상품명");
            headerRow.createCell(1).setCellValue("이미지URL");
            headerRow.createCell(2).setCellValue("가격");
            headerRow.createCell(3).setCellValue("재고");
            headerRow.createCell(4).setCellValue("최소재고");
            headerRow.createCell(5).setCellValue("위치");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("테스트 상품");
            dataRow.createCell(1).setCellValue("https://bucket/item1.jpg");
            dataRow.createCell(2).setCellValue("invalid_price");
            dataRow.createCell(3).setCellValue(50);
            dataRow.createCell(4).setCellValue(5);
            dataRow.createCell(5).setCellValue("a1");

            return convertWorkbookToMultipartFile(workbook, "invalid_format.xlsx");
        }

        private MultipartFile convertWorkbookToMultipartFile(Workbook workbook, String filename)
                throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            return new MockMultipartFile(
                    "itemFile",
                    filename,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    new ByteArrayInputStream(outputStream.toByteArray()));
        }

        private MultipartFile createValidExcelFile() throws IOException {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Items");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("상품명");
            headerRow.createCell(1).setCellValue("이미지URL");
            headerRow.createCell(2).setCellValue("가격");
            headerRow.createCell(3).setCellValue("재고");
            headerRow.createCell(4).setCellValue("최소재고");
            headerRow.createCell(5).setCellValue("위치");

            Row dataRow1 = sheet.createRow(1);
            dataRow1.createCell(0).setCellValue("지수 포토카드");
            dataRow1.createCell(1).setCellValue("https://bucket/item1.jpg");
            dataRow1.createCell(2).setCellValue(5000);
            dataRow1.createCell(3).setCellValue(50);
            dataRow1.createCell(4).setCellValue(5);
            dataRow1.createCell(5).setCellValue("a1");

            Row dataRow2 = sheet.createRow(2);
            dataRow2.createCell(0).setCellValue("제니 포토카드");
            dataRow2.createCell(1).setCellValue("https://bucket/item2.jpg");
            dataRow2.createCell(2).setCellValue(15000);
            dataRow2.createCell(3).setCellValue(100);
            dataRow2.createCell(4).setCellValue(10);
            dataRow2.createCell(5).setCellValue("a2");

            Row dataRow3 = sheet.createRow(3);
            dataRow3.createCell(0).setCellValue("로제 포토카드");
            dataRow3.createCell(1).setCellValue("https://bucket/item3.jpg");
            dataRow3.createCell(2).setCellValue(15000);
            dataRow3.createCell(3).setCellValue(100);
            dataRow3.createCell(4).setCellValue(10);
            dataRow3.createCell(5).setCellValue("a3");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            return new MockMultipartFile(
                    "itemFile",
                    "test_items.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    new ByteArrayInputStream(outputStream.toByteArray()));
        }
    }

    @Nested
    class 상품_목록_조회 {

        @Test
        void 상품_목록_조회에_성공한다() {
            // given
            final Long popupId = popup.getId();

            Item item1 =
                    Item.createItem(
                            popup, "지수 포토카드", "https://bucket/jisoo.jpg", 5000, 50, 5, "a1");
            Item item2 =
                    Item.createItem(
                            popup, "제니 포토카드", "https://bucket/jennie.jpg", 15000, 100, 10, "a2");
            Item item3 =
                    Item.createItem(
                            popup, "로제 포토카드", "https://bucket/rose.jpg", 15000, 100, 10, "b1");

            itemRepository.save(item1);
            itemRepository.save(item2);
            itemRepository.save(item3);

            // when
            Map<String, List<ItemPreviewResponse>> result = itemService.findAllItems(popupId);

            // then
            Assertions.assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result).hasSize(2), // 'a'와 'b' 두 그룹
                    () -> assertThat(result).containsKeys("a", "b"),
                    () -> assertThat(result.get("a")).hasSize(2), // 'a' 그룹에 2개 아이템
                    () -> assertThat(result.get("b")).hasSize(1), // 'b' 그룹에 1개 아이템

                    // a 그룹 첫 번째 아이템 검증
                    () -> assertThat(result.get("a").get(0).location()).isEqualTo("1"),
                    () -> assertThat(result.get("a").get(0).itemId()).isEqualTo(item1.getId()),
                    () -> assertThat(result.get("a").get(0).name()).isEqualTo("지수 포토카드"),
                    () ->
                            assertThat(result.get("a").get(0).imageUrl())
                                    .isEqualTo("https://bucket/jisoo.jpg"),
                    () -> assertThat(result.get("a").get(0).price()).isEqualTo(5000),
                    () -> assertThat(result.get("a").get(0).stock()).isEqualTo(50),
                    () -> assertThat(result.get("a").get(0).minStock()).isEqualTo(5),

                    // a 그룹 두 번째 아이템 검증
                    () -> assertThat(result.get("a").get(1).location()).isEqualTo("2"),
                    () -> assertThat(result.get("a").get(1).itemId()).isEqualTo(item2.getId()),
                    () -> assertThat(result.get("a").get(1).name()).isEqualTo("제니 포토카드"),
                    () ->
                            assertThat(result.get("a").get(1).imageUrl())
                                    .isEqualTo("https://bucket/jennie.jpg"),
                    () -> assertThat(result.get("a").get(1).price()).isEqualTo(15000),
                    () -> assertThat(result.get("a").get(1).stock()).isEqualTo(100),
                    () -> assertThat(result.get("a").get(1).minStock()).isEqualTo(10),

                    // b 그룹 첫 번째 아이템 검증
                    () -> assertThat(result.get("b").get(0).location()).isEqualTo("1"),
                    () -> assertThat(result.get("b").get(0).itemId()).isEqualTo(item3.getId()),
                    () -> assertThat(result.get("b").get(0).name()).isEqualTo("로제 포토카드"),
                    () ->
                            assertThat(result.get("b").get(0).imageUrl())
                                    .isEqualTo("https://bucket/rose.jpg"),
                    () -> assertThat(result.get("b").get(0).price()).isEqualTo(15000),
                    () -> assertThat(result.get("b").get(0).stock()).isEqualTo(100),
                    () -> assertThat(result.get("b").get(0).minStock()).isEqualTo(10));
        }

        @Test
        void 여러_위치에_존재하는_상품_목록을_조회한다() {
            //  given
            Long popupId = popup.getId();

            Item item1 =
                    Item.createItem(
                            popup, "지수 포토카드", "https://bucket/jisoo.jpg", 5000, 50, 5, "a1");
            Item item2 =
                    Item.createItem(
                            popup, "제니 포토카드", "https://bucket/jennie.jpg", 15000, 100, 10, "b1");
            Item item3 =
                    Item.createItem(
                            popup, "로제 포토카드", "https://bucket/rose.jpg", 15000, 100, 10, "c1");
            Item item4 =
                    Item.createItem(
                            popup, "리사 포토카드", "https://bucket/lisa.jpg", 15000, 100, 10, "d1");
            Item item5 =
                    Item.createItem(
                            popup, "블랙핑크 포스터", "https://bucket/blackpink.jpg", 25000, 30, 3, "e1");

            itemRepository.save(item1);
            itemRepository.save(item2);
            itemRepository.save(item3);
            itemRepository.save(item4);
            itemRepository.save(item5);

            // when
            Map<String, List<ItemPreviewResponse>> result = itemService.findAllItems(popupId);

            // then
            Assertions.assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result).hasSize(5), // 'a', 'b', 'c', 'd', 'e' 다섯 그룹
                    () -> assertThat(result).containsKeys("a", "b", "c", "d", "e"),

                    // 각 그룹에 아이템이 1개씩 있는지 확인
                    () -> assertThat(result.get("a")).hasSize(1),
                    () -> assertThat(result.get("b")).hasSize(1),
                    () -> assertThat(result.get("c")).hasSize(1),
                    () -> assertThat(result.get("d")).hasSize(1),
                    () -> assertThat(result.get("e")).hasSize(1),

                    // 각 아이템의 location 값이 올바르게 추출되었는지 확인
                    () -> assertThat(result.get("a").get(0).location()).isEqualTo("1"),
                    () -> assertThat(result.get("b").get(0).location()).isEqualTo("1"),
                    () -> assertThat(result.get("c").get(0).location()).isEqualTo("1"),
                    () -> assertThat(result.get("d").get(0).location()).isEqualTo("1"),
                    () -> assertThat(result.get("e").get(0).location()).isEqualTo("1"),

                    // 각 아이템의 itemId가 원본 Item과 일치하는지 확인
                    () -> assertThat(result.get("a").get(0).itemId()).isEqualTo(item1.getId()),
                    () -> assertThat(result.get("b").get(0).itemId()).isEqualTo(item2.getId()),
                    () -> assertThat(result.get("c").get(0).itemId()).isEqualTo(item3.getId()),
                    () -> assertThat(result.get("d").get(0).itemId()).isEqualTo(item4.getId()),
                    () -> assertThat(result.get("e").get(0).itemId()).isEqualTo(item5.getId()));
        }
    }

    @Nested
    class 상품_삭제 {

        @Test
        void 정상적으로_상품을_삭제한다() {
            // given
            Item savedItem = createTestItem();

            // when
            itemService.deleteItem(popup.getId(), savedItem.getId());

            // then
            assertThat(itemRepository.findById(savedItem.getId())).isEmpty();
        }

        @Test
        void 존재하지_않는_상품을_삭제하면_예외가_발생한다() {
            // given
            Long nonExistentItemId = 9999L;

            // when & then
            assertThatThrownBy(() -> itemService.deleteItem(popup.getId(), nonExistentItemId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ItemErrorCode.ITEM_NOT_FOUND);
        }

        @Test
        void 상품이_해당_팝업에_속하지_않으면_예외가_발생한다() {
            // given
            Item savedItem = createTestItem();

            Long wrongPopupId = otherPopup.getId();

            // when & then
            assertThatThrownBy(() -> itemService.deleteItem(wrongPopupId, savedItem.getId()))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ItemErrorCode.ITEM_POPUP_MISMATCH);
        }
    }

    @Nested
    class 상품_최소_발주_수량_수정 {

        @Test
        void 정상적으로_최소_발주_수량_수정에_성공한다() {
            // given
            Item savedItem = createTestItem(); // 기본 minStock = 10
            final Long popupId = popup.getId();
            final Long itemId = savedItem.getId();
            ItemMinStockUpdateRequest request = new ItemMinStockUpdateRequest(30);

            // when
            ItemDetailResponse response = itemService.updateItemMinStock(popupId, itemId, request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.minStock()).isEqualTo(request.minStock());

            Item updatedItem = itemRepository.findById(itemId).orElseThrow();
            assertThat(updatedItem.getMinStock()).isEqualTo(request.minStock());
        }

        @Test
        void 성공_응답에_알맞은_상품_정보가_포함되어있다() {
            // given
            Item savedItem = createTestItem();
            Long popupId = popup.getId();
            Long itemId = savedItem.getId();
            ItemMinStockUpdateRequest request = new ItemMinStockUpdateRequest(30);

            // when
            ItemDetailResponse response = itemService.updateItemMinStock(popupId, itemId, request);

            // then
            assertThat(response).isNotNull();

            Assertions.assertAll(
                    () -> assertThat(response.id()).isEqualTo(savedItem.getId()),
                    () -> assertThat(response.popupId()).isEqualTo(popupId),
                    () -> assertThat(response.name()).isEqualTo(savedItem.getName()),
                    () -> assertThat(response.imageUrl()).isEqualTo(savedItem.getImageUrl()),
                    () -> assertThat(response.price()).isEqualTo(savedItem.getPrice()),
                    () -> assertThat(response.stock()).isEqualTo(savedItem.getStock()),
                    () -> assertThat(response.minStock()).isEqualTo(request.minStock()),
                    () -> assertThat(response.location()).isEqualTo(savedItem.getLocation()));
        }

        @Test
        void 존재하지_않는_상품의_최소_발주_수량을_수정하면_예외가_발생한다() {
            // given
            final Long popupId = popup.getId();
            final Long nonExistentItemId = 9999L;
            ItemMinStockUpdateRequest request = new ItemMinStockUpdateRequest(30);

            // when & then
            assertThatThrownBy(
                            () ->
                                    itemService.updateItemMinStock(
                                            popupId, nonExistentItemId, request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ItemErrorCode.ITEM_NOT_FOUND);
        }

        @Test
        void 권한이_없는_사용자가_최소_발주_수량을_수정하면_예외가_발생한다() {
            // given
            Item savedItem = createTestItem();
            Long popupId = popup.getId();
            ItemMinStockUpdateRequest request = new ItemMinStockUpdateRequest(30);

            // 다른 관리자로 로그인
            setAuthentication(otherManager);

            // when & then
            assertThatThrownBy(
                            () ->
                                    itemService.updateItemMinStock(
                                            popupId, savedItem.getId(), request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_UNAUTHORIZED);
        }

        @Test
        void 상품이_해당_팝업에_속하지_않으면_예외가_발생한다() {
            // given
            Item savedItem = createTestItem();
            ItemMinStockUpdateRequest request = new ItemMinStockUpdateRequest(30);

            // 다른 팝업 ID 사용
            Long wrongPopupId = otherPopup.getId();

            // when & then
            assertThatThrownBy(
                            () ->
                                    itemService.updateItemMinStock(
                                            wrongPopupId, savedItem.getId(), request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ItemErrorCode.ITEM_POPUP_MISMATCH);
        }

        @Test
        void 최소_발주_수량이_재고보다_크면_예외가_발생한다() {
            // given
            Item savedItem = createTestItem(); // stock = 100
            final Long popupId = popup.getId();
            ItemMinStockUpdateRequest request = new ItemMinStockUpdateRequest(150); // 재고(100)보다 큰 값

            // when & then
            assertThatThrownBy(
                            () ->
                                    itemService.updateItemMinStock(
                                            popupId, savedItem.getId(), request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ItemErrorCode.MIN_STOCK_EXCEEDED);
        }
    }

    @Nested
    class 내부용_상품_목록을_조회할_때 {

        @Test
        void 데이터가_존재하면_상품_목록_조회에_성공한다() {
            // given
            final Long popupId = popup.getId();

            Item item1 =
                    Item.createItem(
                            popup, "지수 포토카드", "https://bucket/jisoo.jpg", 5000, 50, 5, "a1");
            Item item2 =
                    Item.createItem(
                            popup, "제니 포토카드", "https://bucket/jennie.jpg", 15000, 100, 10, "a2");
            Item item3 =
                    Item.createItem(
                            popup, "로제 포토카드", "https://bucket/rose.jpg", 15000, 100, 10, "b1");

            itemRepository.save(item1);
            itemRepository.save(item2);
            itemRepository.save(item3);

            // when
            SliceResponse<ItemInfoResponse> result =
                    itemService.findItemsByNameWithPagination(popupId, null, 4L, 2);

            // then
            Assertions.assertAll(
                    () -> assertThat(result).isNotNull(),
                    () ->
                            assertThat(result.content())
                                    .hasSize(2), // lastItemId = 4 기준으로 id < 4 인 두 개만 조회된다고 가정

                    // 각 항목의 필드 검증 (정렬: id desc)
                    () -> assertThat(result.content().get(0).itemId()).isEqualTo(item3.getId()),
                    () -> assertThat(result.content().get(1).itemId()).isEqualTo(item2.getId()),
                    () -> assertThat(result.isLast()).isFalse() // isLast=false 검증
                    );
        }

        @Test
        void 마지막_상품까지_조회하면_is_last가_true를_반환한다() {
            // given
            final Long popupId = popup.getId();

            Item item1 =
                    Item.createItem(
                            popup, "지수 포토카드", "https://bucket/jisoo.jpg", 5000, 50, 5, "a1");
            Item item2 =
                    Item.createItem(
                            popup, "제니 포토카드", "https://bucket/jennie.jpg", 15000, 100, 10, "a2");
            Item item3 =
                    Item.createItem(
                            popup, "로제 포토카드", "https://bucket/rose.jpg", 15000, 100, 10, "b1");

            itemRepository.save(item1);
            itemRepository.save(item2);
            itemRepository.save(item3);

            // when
            SliceResponse<ItemInfoResponse> result =
                    itemService.findItemsByNameWithPagination(popupId, null, 4L, 3);

            // then
            Assertions.assertAll(
                    () -> assertThat(result).isNotNull(),
                    () ->
                            assertThat(result.content())
                                    .hasSize(3), // lastItemId = 4 기준으로 id < 4 인 세 개만 조회된다고 가정
                    () -> assertThat(result.isLast()).isTrue() // isLast=true 검증
                    );
        }

        @Test
        void 검색어에_대해_결과가_존재하면_상품_검색에_성공한다() {
            // given
            final Long popupId = popup.getId();

            Item item1 =
                    Item.createItem(
                            popup, "지수 포토카드", "https://bucket/jisoo.jpg", 5000, 50, 5, "a1");
            Item item2 =
                    Item.createItem(
                            popup, "제니 포토카드", "https://bucket/jennie.jpg", 15000, 100, 10, "a2");
            Item item3 =
                    Item.createItem(
                            popup, "로제 포토카드", "https://bucket/rose.jpg", 15000, 100, 10, "b1");

            itemRepository.save(item1);
            itemRepository.save(item2);
            itemRepository.save(item3);

            // when
            SliceResponse<ItemInfoResponse> result =
                    itemService.findItemsByNameWithPagination(popupId, "포토카드", 4L, 3);

            // then
            Assertions.assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.content()).hasSize(3),
                    () -> assertThat(result.isLast()).isTrue(),
                    () ->
                            assertThat(result.content())
                                    .allSatisfy(
                                            item ->
                                                    assertThat(item.name())
                                                            .contains("포토카드")) // 검색 결과 검증
                    );
        }

        @Test
        void 검색어에_대해_결과가_없으면_빈_리스트를_반환한다() {
            // given
            final Long popupId = popup.getId();

            Item item1 =
                    Item.createItem(
                            popup, "지수 포토카드", "https://bucket/jisoo.jpg", 5000, 50, 5, "a1");

            itemRepository.save(item1);

            // when
            SliceResponse<ItemInfoResponse> result =
                    itemService.findItemsByNameWithPagination(popupId, "키링", null, 3);

            // then
            Assertions.assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.content()).isEmpty(), // 빈 리스트 검증
                    () -> assertThat(result.isLast()).isTrue());
        }
    }

    @Nested
    class 내부용_기본_상품_목록을_조회할_때 {

        @Test
        void 무작위로_선택된_4개의_상품_조회에_성공한다() {
            // given
            final Long popupId = popup.getId();

            Item item1 =
                    Item.createItem(
                            popup, "지수 포토카드", "https://bucket/jisoo.jpg", 5000, 50, 5, "a1");
            Item item2 =
                    Item.createItem(
                            popup, "제니 포토카드", "https://bucket/jennie.jpg", 15000, 100, 10, "a2");
            Item item3 =
                    Item.createItem(
                            popup, "로제 포토카드", "https://bucket/rose.jpg", 15000, 100, 10, "b1");
            Item item4 =
                    Item.createItem(popup, "리사 포토카드", "https://bucket/lisa.jpg", 5000, 50, 5, "b2");

            itemRepository.save(item1);
            itemRepository.save(item2);
            itemRepository.save(item3);
            itemRepository.save(item4);

            // when
            List<ItemInfoResponse> result = itemService.findRandomItems(popupId);

            // then
            Assertions.assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result).hasSize(4),
                    () -> assertThat(new HashSet<>(result)).hasSize(result.size()));
        }

        @Test
        void 상품_데이터가_4개보다_적으면_전체_데이터_수_만큼_상품이_조회된다() {
            // given
            final Long popupId = popup.getId();

            Item item1 =
                    Item.createItem(
                            popup, "지수 포토카드", "https://bucket/jisoo.jpg", 5000, 50, 5, "a1");
            Item item2 =
                    Item.createItem(
                            popup, "제니 포토카드", "https://bucket/jennie.jpg", 15000, 100, 10, "a2");
            Item item3 =
                    Item.createItem(
                            popup, "로제 포토카드", "https://bucket/rose.jpg", 15000, 100, 10, "b1");

            itemRepository.save(item1);
            itemRepository.save(item2);
            itemRepository.save(item3);

            // when
            List<ItemInfoResponse> result = itemService.findRandomItems(popupId);

            // then
            Assertions.assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result).hasSize(3),
                    () -> assertThat(new HashSet<>(result)).hasSize(result.size()));
        }
    }

    @Nested
    class 결제_준비용_상품_상세_목록을_조회할_때 {

        @Test
        void 요청한_itemId_목록에_해당하는_상품_정보를_반환한다() {
            // given
            final Long popupId = popup.getId();

            Item item1 =
                    Item.createItem(
                            popup, "지수 포토카드", "https://bucket/jisoo.jpg", 5000, 50, 5, "a1");
            Item item2 =
                    Item.createItem(
                            popup, "제니 포토카드", "https://bucket/jennie.jpg", 15000, 100, 10, "a2");
            itemRepository.saveAll(List.of(item1, item2));

            ItemIdsForPaymentRequest request =
                    new ItemIdsForPaymentRequest(List.of(item1.getId(), item2.getId()));

            // when
            List<ItemForPaymentResponse> result = itemService.findItemsForPayment(popupId, request);

            // then
            Assertions.assertAll(
                    () -> assertThat(result).hasSize(2),
                    () ->
                            assertThat(result)
                                    .extracting("itemId", "name", "price", "stock")
                                    .containsExactlyInAnyOrder(
                                            tuple(
                                                    item1.getId(),
                                                    item1.getName(),
                                                    item1.getPrice(),
                                                    item1.getStock()),
                                            tuple(
                                                    item2.getId(),
                                                    item2.getName(),
                                                    item2.getPrice(),
                                                    item2.getStock())));
        }

        @Test
        void 존재하지_않는_itemId가_포함된_경우_해당_상품을_제외하고_존재하는_상품_정보만_반환한다() {
            // given
            final Long popupId = popup.getId();

            Item item1 =
                    Item.createItem(
                            popup, "지수 포토카드", "https://bucket/jisoo.jpg", 5000, 50, 5, "a1");
            Item item2 =
                    Item.createItem(
                            popup, "제니 포토카드", "https://bucket/jennie.jpg", 15000, 100, 10, "a2");
            Item item3 =
                    Item.createItem(
                            popup, "로제 포토카드", "https://bucket/rose.jpg", 15000, 100, 10, "b1");
            itemRepository.saveAll(List.of(item1, item2, item3));

            ItemIdsForPaymentRequest request =
                    new ItemIdsForPaymentRequest(List.of(item1.getId(), 999L, item3.getId()));

            // when
            List<ItemForPaymentResponse> result = itemService.findItemsForPayment(popupId, request);

            // then
            Assertions.assertAll(
                    () -> assertThat(result).hasSize(2),
                    () ->
                            assertThat(result)
                                    .extracting("itemId", "name", "price", "stock")
                                    .containsExactlyInAnyOrder(
                                            tuple(
                                                    item1.getId(),
                                                    item1.getName(),
                                                    item1.getPrice(),
                                                    item1.getStock()),
                                            tuple(
                                                    item3.getId(),
                                                    item3.getName(),
                                                    item3.getPrice(),
                                                    item3.getStock())));
        }
    }

    @Nested
    class 인기_상품_조회할_때 {
        @BeforeEach
        void setUp() {
            item1 =
                    Item.createItem(
                            popup, "지수 포토카드", "https://bucket/jisoo.jpg", 5000, 50, 5, "a1");
            item2 =
                    Item.createItem(
                            popup, "제니 포토카드", "https://bucket/jennie.jpg", 15000, 100, 10, "a2");
            item3 =
                    Item.createItem(
                            popup, "로제 포토카드", "https://bucket/rose.jpg", 15000, 100, 10, "b1");

            itemRepository.save(item1);
            itemRepository.save(item2);
            itemRepository.save(item3);
        }

        @BeforeEach
        void setUpForTrendingItems() {
            item1.updatePopularityScore(100);
            item2.updatePopularityScore(80);
            item3.updatePopularityScore(60);
            item1.decreaseStockAndIncreaseSales(50);
            item2.decreaseStockAndIncreaseSales(30);
            item3.decreaseStockAndIncreaseSales(20);
            itemRepository.saveAll(List.of(item1, item2, item3));
        }

        @Test
        void 정상적으로_인기_상품_TOP3를_조회한다() {
            // given
            Long popupId = popup.getId();

            // when
            List<ItemTrendingResponse> trendingItems =
                    itemService.getTrendingItemsByManager(popupId);

            // then
            AssertionsForClassTypes.assertThat(trendingItems).isNotNull();
            AssertionsForClassTypes.assertThat(trendingItems.size()).isEqualTo(3); // 3개 상품만 있음

            AssertionsForClassTypes.assertThat(trendingItems.get(0).itemId())
                    .isEqualTo(item1.getId()); // 100 + 50 = 150
            AssertionsForClassTypes.assertThat(trendingItems.get(1).itemId())
                    .isEqualTo(item2.getId()); // 80 + 30 = 110
            AssertionsForClassTypes.assertThat(trendingItems.get(2).itemId())
                    .isEqualTo(item3.getId()); // 60 + 20 = 80
        }

        @Test
        void 상품_분석_데이터가_없으면_빈_리스트를_반환한다() {
            // given
            Long popupId = otherPopup.getId();

            // when
            List<ItemTrendingResponse> trendingItems =
                    itemService.getTrendingItemsByManager(popupId);

            // then
            AssertionsForClassTypes.assertThat(trendingItems).isNotNull();
            AssertionsForClassTypes.assertThat(trendingItems.size()).isEqualTo(0);
        }

        @Test
        void 권한이_없는_사용자가_인기_상품을_조회하면_예외가_발생한다() {
            // given
            Long popupId = popup.getId();
            setAuthentication(otherManager);

            // when & then
            AssertionsForClassTypes.assertThatThrownBy(
                            () -> itemService.getTrendingItemsByManager(popupId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_UNAUTHORIZED);
        }

        @Test
        void 존재하지_않는_팝업에_대해_인기_상품을_조회하면_예외가_발생한다() {
            // given
            Long nonExistentPopupId = 9999L;

            // when & then
            AssertionsForClassTypes.assertThatThrownBy(
                            () -> itemService.getTrendingItemsByManager(nonExistentPopupId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_NOT_FOUND);
        }
    }

    @Nested
    class 내부용_인기_상품_조회할_때 {
        @BeforeEach
        void setUp() {
            item1 =
                    Item.createItem(
                            popup, "지수 포토카드", "https://bucket/jisoo.jpg", 5000, 50, 5, "a1");
            item2 =
                    Item.createItem(
                            popup, "제니 포토카드", "https://bucket/jennie.jpg", 15000, 100, 10, "a2");
            item3 =
                    Item.createItem(
                            popup, "로제 포토카드", "https://bucket/rose.jpg", 15000, 100, 10, "b1");

            itemRepository.save(item1);
            itemRepository.save(item2);
            itemRepository.save(item3);
        }

        @BeforeEach
        void setUpForTrendingItems() {
            item1.updatePopularityScore(100);
            item2.updatePopularityScore(80);
            item3.updatePopularityScore(60);
            item1.decreaseStockAndIncreaseSales(50);
            item2.decreaseStockAndIncreaseSales(30);
            item3.decreaseStockAndIncreaseSales(20);
            itemRepository.saveAll(List.of(item1, item2, item3));
        }

        @Test
        void 정상적으로_인기_상품_TOP3를_조회한다() {
            // given
            Long popupId = popup.getId();

            // when
            List<ItemTrendingResponse> trendingItems =
                    itemService.getTrendingItemsByManager(popupId);

            // then
            AssertionsForClassTypes.assertThat(trendingItems).isNotNull();
            AssertionsForClassTypes.assertThat(trendingItems.size()).isEqualTo(3); // 3개 상품만 있음

            AssertionsForClassTypes.assertThat(trendingItems.get(0).itemId())
                    .isEqualTo(item1.getId());
            AssertionsForClassTypes.assertThat(trendingItems.get(1).itemId())
                    .isEqualTo(item2.getId());
            AssertionsForClassTypes.assertThat(trendingItems.get(2).itemId())
                    .isEqualTo(item3.getId());
        }

        @Test
        void 상품_분석_데이터가_없으면_빈_리스트를_반환한다() {
            // given
            Long popupId = otherPopup.getId();

            // when
            List<ItemTrendingResponse> trendingItems =
                    itemService.getTrendingItemsByManager(popupId);

            // then
            AssertionsForClassTypes.assertThat(trendingItems).isNotNull();
            AssertionsForClassTypes.assertThat(trendingItems.size()).isEqualTo(0);
        }

        @Test
        void 존재하지_않는_팝업에_대해_인기_상품을_조회하면_예외가_발생한다() {
            // given
            Long nonExistentPopupId = 9999L;

            // when & then
            AssertionsForClassTypes.assertThatThrownBy(
                            () -> itemService.getTrendingItemsByManager(nonExistentPopupId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_NOT_FOUND);
        }
    }

    private Item createTestItem() {
        return itemRepository.save(
                Item.createItem(popup, "테스트 상품", "https://bucket/item.jpg", 10000, 100, 10, "a1"));
    }
}
