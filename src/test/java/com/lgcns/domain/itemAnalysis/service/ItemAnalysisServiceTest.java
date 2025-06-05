package com.lgcns.domain.itemAnalysis.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.itemAnalysis.domain.ItemAnalysis;
import com.lgcns.domain.itemAnalysis.dto.response.ItemTrendingResponse;
import com.lgcns.domain.itemAnalysis.repository.ItemAnalysisRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ItemAnalysisServiceTest extends IntegrationTest {

    @Autowired private ItemAnalysisService itemAnalysisService;
    @Autowired private ItemRepository itemRepository;
    @Autowired private PopupRepository popupRepository;
    @Autowired private ManagerRepository managerRepository;
    @Autowired private ItemAnalysisRepository itemAnalysisRepository;

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

        popup = createTestPopup(ownerManager, "2025-01-01", LocalDate.now().plusDays(5).toString());
        popup = popupRepository.save(popup);

        otherPopup =
                createTestPopup(ownerManager, "2025-02-01", LocalDate.now().plusDays(5).toString());
        otherPopup = popupRepository.save(otherPopup);

        item1 = Item.createItem(popup, "지수 포토카드", "https://bucket/jisoo.jpg", 5000, 50, 5, "a1");
        item2 =
                Item.createItem(
                        popup, "제니 포토카드", "https://bucket/jennie.jpg", 15000, 100, 10, "a2");
        item3 = Item.createItem(popup, "로제 포토카드", "https://bucket/rose.jpg", 15000, 100, 10, "b1");

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }

    @Nested
    class 인기_상품_조회할_때 {

        @BeforeEach
        void setUpForTrendingItems() {
            itemAnalysisRepository.saveAll(
                    List.of(
                            ItemAnalysis.createItemAnalysis(item1, 100, 0.0, 50),
                            ItemAnalysis.createItemAnalysis(item2, 80, 0.0, 30),
                            ItemAnalysis.createItemAnalysis(item3, 60, 0.0, 20)));
        }

        @Test
        void 정상적으로_인기_상품_TOP3를_조회한다() {
            // given
            Long popupId = popup.getId();

            // when
            List<ItemTrendingResponse> trendingItems =
                    itemAnalysisService.getTrendingItems(popupId);

            // then
            assertThat(trendingItems).isNotNull();
            assertThat(trendingItems.size()).isEqualTo(3); // 3개 상품만 있음

            assertThat(trendingItems.get(0).itemId()).isEqualTo(item1.getId()); // 100 + 50 = 150
            assertThat(trendingItems.get(1).itemId()).isEqualTo(item2.getId()); // 80 + 30 = 110
            assertThat(trendingItems.get(2).itemId()).isEqualTo(item3.getId()); // 60 + 20 = 80
        }

        @Test
        void 상품_분석_데이터가_없으면_빈_리스트를_반환한다() {
            // given
            Long popupId = otherPopup.getId();

            // when
            List<ItemTrendingResponse> trendingItems =
                    itemAnalysisService.getTrendingItems(popupId);

            // then
            assertThat(trendingItems).isNotNull();
            assertThat(trendingItems.size()).isEqualTo(0);
        }

        @Test
        void 권한이_없는_사용자가_인기_상품을_조회하면_예외가_발생한다() {
            // given
            Long popupId = popup.getId();
            setAuthentication(otherManager);

            // when & then
            assertThatThrownBy(() -> itemAnalysisService.getTrendingItems(popupId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_UNAUTHORIZED);
        }

        @Test
        void 존재하지_않는_팝업에_대해_인기_상품을_조회하면_예외가_발생한다() {
            // given
            Long nonExistentPopupId = 9999L;

            // when & then
            assertThatThrownBy(() -> itemAnalysisService.getTrendingItems(nonExistentPopupId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_NOT_FOUND);
        }
    }

    private Popup createTestPopup(Manager manager, String startDate, String endDate) {
        return Popup.createPopup(
                manager,
                "testPopup",
                "https://bucket/이미지.jpg",
                LocalDate.parse(startDate),
                LocalDate.parse(endDate),
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
    }
}
