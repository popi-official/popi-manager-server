package com.lgcns.domain.item.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.dto.request.ItemCreateRequest;
import com.lgcns.domain.item.dto.response.ItemPreviewResponse;
import com.lgcns.domain.item.exception.ItemErrorCode;
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
import java.util.Map;
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

    private Item createTestItem() {
        return itemRepository.save(
                Item.createItem(popup, "테스트 상품", "https://bucket/item.jpg", 10000, 100, 10, "a1"));
    }

    // 새로운 관리자를 생성하고 해당 관리자로 로그인 상태로 변경
    private Manager loginAsNewManager(String username) {
        Manager newManager =
                managerRepository.save(Manager.createManager(username, "testPassword"));
        loginAs(newManager);
        return newManager;
    }

    // 지정된 관리자로 로그인 상태로 변경
    private void loginAs(Manager manager) {
        UserDetails userDetails = new PrincipalDetails(manager.getId(), manager.getRole(), null);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
    }

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
        @Transactional
        void 권한이_없는_사용자가_상품을_등록하면_예외가_발생한다() {
            // give
            Manager otherManager = loginAsNewManager("otherManager");

            ItemCreateRequest request = createItemCreateRequest();

            // when & then
            assertThatThrownBy(() -> itemService.createItem(popup.getId(), request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_UNAUTHORIZED);
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

        @Test
        @Transactional
        void 권한이_없는_사용자가_엑셀로_상품을_등록하면_예외가_발생한다() throws IOException {
            // given
            Manager otherManager = loginAsNewManager("otherManager");

            MultipartFile excelFile = createExcelFile();

            // when & then
            assertThatThrownBy(() -> itemService.createItemByExcel(popup.getId(), excelFile))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_UNAUTHORIZED);
        }
    }

    @Nested
    class 상품_목록_조회 {
        @Test
        @Transactional
        void 상품_목록_조회에_성공한다() {
            // given
            Long popupId = popup.getId();

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
        @Transactional
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
        @Transactional
        void 정상적으로_상품을_삭제한다() {
            // given
            Item savedItem = createTestItem();

            // when
            itemService.deleteItem(popup.getId(), savedItem.getId());

            // then
            assertThat(itemRepository.findById(savedItem.getId())).isEmpty();
        }

        @Test
        @Transactional
        void 존재하지_않는_상품을_삭제하면_예외가_발생한다() {
            // given
            Long nonExistentItemId = 9999L;

            // when & then
            assertThatThrownBy(() -> itemService.deleteItem(popup.getId(), nonExistentItemId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ItemErrorCode.ITEM_NOT_FOUND);
        }

        @Test
        @Transactional
        void 상품이_해당_팝업에_속하지_않으면_예외가_발생한다() {
            // given
            Item savedItem = createTestItem();

            Popup anotherPopup =
                    Popup.createPopup(
                            manager, // 동일한 관리자
                            "anotherPopup",
                            "https://bucket/another.jpg",
                            LocalDate.parse("2025-01-01"),
                            LocalDate.parse("2025-01-31"),
                            LocalDateTime.parse("2025-01-01T10:00:00"),
                            LocalDateTime.parse("2025-01-31T20:00:00"),
                            LocalTime.parse("10:00:00"),
                            LocalTime.parse("20:00:00"),
                            100,
                            20,
                            "서울특별시 강남구 테헤란로 456",
                            "5층 B호",
                            37.654321,
                            127.654321);
            Popup savedAnotherPopup = popupRepository.save(anotherPopup);

            Long wrongPopupId = savedAnotherPopup.getId();

            // when & then
            assertThatThrownBy(() -> itemService.deleteItem(wrongPopupId, savedItem.getId()))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode", ItemErrorCode.ITEM_NOT_FOUND_IN_POPUP);
        }
    }
}
