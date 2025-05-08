package com.lgcns.domain.item.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.dto.request.ItemCreateRequest;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.security.PrincipalDetails;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

class ItemServiceTest extends IntegrationTest {
    @Autowired ItemService itemService;
    @Autowired ItemRepository itemRepository;
    @Autowired PopupRepository popupRepository;
    @Autowired ManagerRepository managerRepository;

    private Manager manager;
    private Popup popup;

    @BeforeEach
    void setUp() {
        manager = managerRepository.save(Manager.createManager("testManager1", "testPassword"));

        UserDetails userDetails = new PrincipalDetails(manager.getId(), manager.getRole(), null);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);

        popup =
                Popup.createPopup(
                        manager,
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
    }

    @Nested
    class 상품_등록 {
        @Test
        @Transactional
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
        @Transactional
        void 존재하지_않는_팝업_ID로_상품_등록을_시도하면_실패한다() {
            // given
            final Long popupId = 9999L;
            ItemCreateRequest request =
                    new ItemCreateRequest(
                            "테스트 상품", "https://bucket/item.jpg", 10000, 100, 10, "a1");

            // when & then
            assertThatThrownBy(() -> itemService.createItem(popupId, request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_NOT_FOUND);
        }

        private ItemCreateRequest createItemCreateRequest() {
            return new ItemCreateRequest("테스트 상품", "https://bucket/item.jpg", 10000, 100, 10, "a1");
        }
    }

    @Nested
    class 엑셀_파일_상품_등록 {
        @Test
        @Transactional
        void 유효한_엑셀파일로_상품_등록에_성공한다() throws IOException {
            // given
            MultipartFile excelFile = createExcelFile();

            // when
            try {
                itemService.createItemByExcel(popup.getId(), excelFile);
            } catch (Exception e) {
                e.printStackTrace();
                Assertions.fail("엑셀 업로드 실패: " + e.getMessage());
            }

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
        @Transactional
        void 존재하지_않는_팝업_ID로_엑셀_상품_등록을_시도하면_실패한다() throws IOException {
            // given
            MultipartFile excelFile = createExcelFile();
            Long nonExistentPopupId = 9999L;

            // when & then
            assertThatThrownBy(() -> itemService.createItemByExcel(nonExistentPopupId, excelFile))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_NOT_FOUND);
        }

        private MultipartFile createExcelFile() throws IOException {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Items");

            // 헤더
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("상품명");
            headerRow.createCell(1).setCellValue("이미지URL");
            headerRow.createCell(2).setCellValue("가격");
            headerRow.createCell(3).setCellValue("재고");
            headerRow.createCell(4).setCellValue("최소재고");
            headerRow.createCell(5).setCellValue("위치");

            // 데이터
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

            // 엑셀파일 -> 바이트 배열
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
}
