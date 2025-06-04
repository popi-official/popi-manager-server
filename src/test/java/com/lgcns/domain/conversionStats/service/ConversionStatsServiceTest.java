package com.lgcns.domain.conversionStats.service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgcns.IntegrationTest;
import com.lgcns.domain.conversionStats.domain.ConversionStats;
import com.lgcns.domain.conversionStats.dto.response.ConversionItem;
import com.lgcns.domain.conversionStats.dto.response.ConversionItemsResponse;
import com.lgcns.domain.conversionStats.repository.ConversionStatsRepository;
import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.repository.PopupRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;

class ConversionStatsServiceTest extends IntegrationTest {

    @Autowired ConversionStatsService conversionStatsService;
    @Autowired ConversionStatsRepository conversionStatsRepository;
    @Autowired ItemRepository itemRepository;
    @Autowired PopupRepository popupRepository;
    @Autowired ManagerRepository managerRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Manager manager;

    @Nested
    class 구매_전환율을_조회할_때 {

        @BeforeEach
        void setUp() {
            manager = managerRepository.save(Manager.createManager("testManager", "testPassword"));

            setAuthentication(manager);

            Popup popup = popupRepository.save(createTestPopup(manager, "popup1"));

            List<Item> items = new ArrayList<>();
            for (int i = 1; i <= 24; i++) {
                items.add(
                        Item.createItem(
                                popup,
                                "ITEM " + i,
                                "https://bucket/item" + i + ".jpg",
                                10000,
                                100,
                                10,
                                "a" + i));
            }
            itemRepository.saveAll(items);

            List<ConversionStats> stats = new ArrayList<>();
            for (long i = 1; i <= 24; i++) {
                int interested = 100 + (int) (Math.random() * 100);
                int buyer = (int) (Math.random() * interested);
                int rate = interested == 0 ? 0 : (buyer * 100) / interested;

                stats.add(
                        ConversionStats.createConversionStats(
                                1L, i, interested, buyer, rate, LocalDate.now(), LocalTime.now()));
            }
            conversionStatsRepository.saveAll(stats);
        }

        @Test
        @Commit
        void 정상적으로_구매_전환율_조회에_성공한다() {
            // given
            Long popupId = 1L;

            // when
            System.out.println("now from service: " + LocalDate.now());
            conversionStatsRepository
                    .findAll()
                    .forEach(
                            cs -> {
                                System.out.printf(
                                        "popupId: %d, itemId: %d, date: %s%n",
                                        cs.getPopupId(), cs.getItemId(), cs.getAnalyzedDate());
                            });
            ConversionItemsResponse response = conversionStatsService.findConversionItems(popupId);

            // then
            assertThat(response.low()).hasSize(6);
            assertThat(response.high()).hasSize(6);
            assertThat(response.low())
                    .isSortedAccordingTo(Comparator.comparingInt(ConversionItem::conversionRatio));
            assertThat(response.high())
                    .isSortedAccordingTo(
                            Comparator.comparingInt(ConversionItem::conversionRatio).reversed());
        }

        @Test
        void 데이터가_존재하지_않으면_빈_리스트를_반환한다() {
            // given
            conversionStatsRepository.deleteAll();

            // when
            ConversionItemsResponse response = conversionStatsService.findConversionItems(1L);

            // then
            assertThat(response.low()).isEmpty();
            assertThat(response.high()).isEmpty();
        }
    }

    private Popup createTestPopup(Manager manager, String name) {
        return Popup.createPopup(
                manager,
                name,
                "https://bucket/이미지.jpg",
                LocalDate.now().minusMonths(1),
                LocalDate.parse("2025-05-01"),
                LocalDateTime.of(LocalDate.now().minusMonths(1), LocalTime.of(6, 0)),
                LocalDateTime.parse("2025-05-01T22:00:00"),
                LocalTime.parse("06:00:00"),
                LocalTime.parse("22:00:00"),
                100,
                20,
                "서울특별시 강남구 테헤란로 123",
                "3층 A호",
                37.123456,
                127.123456);
    }
}
