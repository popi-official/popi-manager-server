package com.lgcns.domain.itemAnalysis.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.itemAnalysis.domain.ItemAnalysis;
import com.lgcns.domain.itemAnalysis.domain.ItemSalesStats;
import com.lgcns.domain.itemAnalysis.dto.response.ItemTrendingResponse;
import com.lgcns.domain.itemAnalysis.dto.response.PopupEventResponse;
import com.lgcns.domain.itemAnalysis.repository.DynamoDBRepository;
import com.lgcns.domain.itemAnalysis.repository.ItemAnalysisRepository;
import com.lgcns.domain.itemAnalysis.repository.ItemSalesStatsRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class ItemAnalysisServiceTest extends IntegrationTest {

    @Autowired private ItemRepository itemRepository;
    @Autowired private PopupRepository popupRepository;
    @Autowired private ManagerRepository managerRepository;
    @Autowired private ItemSalesStatsRepository itemSalesStatsRepository;
    @Autowired private ItemAnalysisRepository itemAnalysisRepository;
    @Autowired private ManagerUtil managerUtil;
    @Mock private DynamoDBRepository dynamoDBRepository;

    private ItemAnalysisService itemAnalysisService;

    private Manager ownerManager;
    private Manager otherManager;
    private Popup popup;
    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 실제 서비스에 Mock 객체 주입
        itemAnalysisService =
                new ItemAnalysisServiceImpl(
                        dynamoDBRepository,
                        itemSalesStatsRepository,
                        itemRepository,
                        popupRepository,
                        managerUtil,
                        itemAnalysisRepository);

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

        item1 = Item.createItem(popup, "지수 포토카드", "https://bucket/jisoo.jpg", 5000, 50, 5, "a1");
        item2 =
                Item.createItem(
                        popup, "제니 포토카드", "https://bucket/jennie.jpg", 15000, 100, 10, "a2");
        item3 = Item.createItem(popup, "로제 포토카드", "https://bucket/rose.jpg", 15000, 100, 10, "b1");

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        itemSalesStatsRepository.save(
                ItemSalesStats.createItemSalesStats(popup.getId(), item1.getId(), 50));
        itemSalesStatsRepository.save(
                ItemSalesStats.createItemSalesStats(popup.getId(), item2.getId(), 30));
        itemSalesStatsRepository.save(
                ItemSalesStats.createItemSalesStats(popup.getId(), item3.getId(), 20));
    }

    @Nested
    class 인기_상품_조회 {

        @Test
        @Transactional
        void 정상적으로_인기_상품_TOP3를_조회한다() {
            // given
            Long popupId = popup.getId();
            List<PopupEventResponse> mockEvents = createMockPopupEvents(popupId);
            when(dynamoDBRepository.getEventsUntilTime(anyLong(), any(LocalDateTime.class)))
                    .thenReturn(mockEvents);

            // when
            List<ItemTrendingResponse> trendingItems =
                    itemAnalysisService.getTrendingItems(popupId);

            // then
            assertThat(trendingItems).isNotNull();

            ItemAnalysis item1Analysis =
                    itemAnalysisRepository.findByItemId(item1.getId()).orElseThrow();
            assertThat(item1Analysis.getPopularityScore()).isEqualTo(40);
            assertThat(item1Analysis.getSalesVolume()).isEqualTo(50);

            ItemAnalysis item2Analysis =
                    itemAnalysisRepository.findByItemId(item2.getId()).orElseThrow();
            assertThat(item2Analysis.getPopularityScore()).isEqualTo(30);
            assertThat(item2Analysis.getSalesVolume()).isEqualTo(30);

            ItemAnalysis item3Analysis =
                    itemAnalysisRepository.findByItemId(item3.getId()).orElseThrow();
            assertThat(item3Analysis.getPopularityScore()).isEqualTo(20);
            assertThat(item3Analysis.getSalesVolume()).isEqualTo(20);

            assertThat(trendingItems.get(0).itemId()).isEqualTo(item1.getId());
            assertThat(trendingItems.get(1).itemId()).isEqualTo(item2.getId());
            assertThat(trendingItems.get(2).itemId()).isEqualTo(item3.getId());
        }

        @Test
        @Transactional
        void 이벤트가_없을_때_판매량만으로_인기_상품을_조회한다() {
            // given
            Long popupId = popup.getId();

            // 빈 이벤트 리스트 반환
            when(dynamoDBRepository.getEventsUntilTime(anyLong(), any(LocalDateTime.class)))
                    .thenReturn(new ArrayList<>());

            // when
            List<ItemTrendingResponse> trendingItems =
                    itemAnalysisService.getTrendingItems(popupId);

            // then
            assertThat(trendingItems).isNotNull();
            ItemAnalysis item1Analysis =
                    itemAnalysisRepository.findByItemId(item1.getId()).orElseThrow();
            assertThat(item1Analysis.getPopularityScore()).isEqualTo(0); // 이벤트 없음
            assertThat(item1Analysis.getSalesVolume()).isEqualTo(50);

            ItemAnalysis item2Analysis =
                    itemAnalysisRepository.findByItemId(item2.getId()).orElseThrow();
            assertThat(item2Analysis.getPopularityScore()).isEqualTo(0);
            assertThat(item2Analysis.getSalesVolume()).isEqualTo(30);

            ItemAnalysis item3Analysis =
                    itemAnalysisRepository.findByItemId(item3.getId()).orElseThrow();
            assertThat(item3Analysis.getPopularityScore()).isEqualTo(0);
            assertThat(item3Analysis.getSalesVolume()).isEqualTo(20);

            // 판매량만으로 순서 결정: item1(50), item2(30), item3(20)
            assertThat(trendingItems.get(0).itemId()).isEqualTo(item1.getId());
            assertThat(trendingItems.get(1).itemId()).isEqualTo(item2.getId());
            assertThat(trendingItems.get(2).itemId()).isEqualTo(item3.getId());
        }

        @Test
        @Transactional
        void 판매량이_없을_때_인기도만으로_인기_상품을_조회한다() {
            // given
            Long popupId = popup.getId();
            itemSalesStatsRepository.deleteAll();

            List<PopupEventResponse> mockEvents = createMockPopupEvents(popupId);
            when(dynamoDBRepository.getEventsUntilTime(anyLong(), any(LocalDateTime.class)))
                    .thenReturn(mockEvents);

            // when
            List<ItemTrendingResponse> trendingItems =
                    itemAnalysisService.getTrendingItems(popupId);

            // then
            assertThat(trendingItems).isNotNull();

            ItemAnalysis item1Analysis =
                    itemAnalysisRepository.findByItemId(item1.getId()).orElseThrow();
            assertThat(item1Analysis.getPopularityScore()).isEqualTo(40);
            assertThat(item1Analysis.getSalesVolume()).isEqualTo(0); // 판매 데이터 없음

            ItemAnalysis item2Analysis =
                    itemAnalysisRepository.findByItemId(item2.getId()).orElseThrow();
            assertThat(item2Analysis.getPopularityScore()).isEqualTo(30);
            assertThat(item2Analysis.getSalesVolume()).isEqualTo(0);

            ItemAnalysis item3Analysis =
                    itemAnalysisRepository.findByItemId(item3.getId()).orElseThrow();
            assertThat(item3Analysis.getPopularityScore()).isEqualTo(20);
            assertThat(item3Analysis.getSalesVolume()).isEqualTo(0);

            // 인기도만으로 순서 결정: item1(40), item2(30), item3(20)
            assertThat(trendingItems.get(0).itemId()).isEqualTo(item1.getId());
            assertThat(trendingItems.get(1).itemId()).isEqualTo(item2.getId());
            assertThat(trendingItems.get(2).itemId()).isEqualTo(item3.getId());
        }

        @Test
        @Transactional
        void 인기_상품이_없을_때_빈_리스트를_반환한다() {
            // given
            Long popupId = popup.getId();

            // 상품 삭제
            itemRepository.deleteAll();
            itemAnalysisRepository.deleteAll();
            itemSalesStatsRepository.deleteAll();

            // 빈 이벤트 리스트 반환
            when(dynamoDBRepository.getEventsUntilTime(anyLong(), any(LocalDateTime.class)))
                    .thenReturn(new ArrayList<>());

            // when
            List<ItemTrendingResponse> trendingItems =
                    itemAnalysisService.getTrendingItems(popupId);

            // then
            assertThat(trendingItems).isNotNull();
        }

        @Test
        @Transactional
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
        @Transactional
        void 존재하지_않는_팝업에_대해_인기_상품을_조회하면_예외가_발생한다() {
            // given
            Long nonExistentPopupId = 9999L;

            // when & then
            assertThatThrownBy(() -> itemAnalysisService.getTrendingItems(nonExistentPopupId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PopupErrorCode.POPUP_NOT_FOUND);
        }
    }

    private List<PopupEventResponse> createMockPopupEvents(Long popupId) {
        List<PopupEventResponse> events = new ArrayList<>();

        // item1에 대한 이벤트 (점수 총합 40)
        PopupEventResponse event1 = new PopupEventResponse();
        event1.setPopupId(popupId.toString());
        event1.setEventKey("2025-01-10T12:00:00Z#stay");
        event1.setItemId(item1.getId());
        event1.setScore(10);
        events.add(event1);

        PopupEventResponse event2 = new PopupEventResponse();
        event2.setPopupId(popupId.toString());
        event2.setEventKey("2025-01-10T13:00:00Z#stay");
        event2.setItemId(item1.getId());
        event2.setScore(30);
        events.add(event2);

        // item2에 대한 이벤트 (점수 총합 30)
        PopupEventResponse event3 = new PopupEventResponse();
        event3.setPopupId(popupId.toString());
        event3.setEventKey("2025-01-10T14:00:00Z#stay");
        event3.setItemId(item2.getId());
        event3.setScore(30);
        events.add(event3);

        // item3에 대한 이벤트 (점수 총합 20)
        PopupEventResponse event4 = new PopupEventResponse();
        event4.setPopupId(popupId.toString());
        event4.setEventKey("2025-01-10T15:00:00Z#stay");
        event4.setItemId(item3.getId());
        event4.setScore(20);
        events.add(event4);

        return events;
    }
}
