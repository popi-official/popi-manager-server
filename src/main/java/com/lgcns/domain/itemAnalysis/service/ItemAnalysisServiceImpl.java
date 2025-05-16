package com.lgcns.domain.itemAnalysis.service;

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
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemAnalysisServiceImpl implements ItemAnalysisService {

    private final DynamoDBRepository dynamoDBRepository;
    private final ItemSalesStatsRepository itemSalesStatsRepository;
    private final ItemRepository itemRepository;
    private final PopupRepository popupRepository;
    private final ManagerUtil managerUtil;
    private final ItemAnalysisRepository itemAnalysisRepository;

    @Override
    @Transactional
    public List<ItemTrendingResponse> getTrendingItems(Long popupId) {
        final Manager currentManager = managerUtil.getCurrentManager();
        final Popup popup = findPopupById(popupId);

        validatePopupOwnership(currentManager, popup);

        LocalDateTime endTime = calculatePreviousTimeEnd(LocalDateTime.now());

        Map<Long, Integer> popularityMap = getItemPopularityScores(popupId, endTime);
        Map<Long, Integer> salesMap = getItemSalesVolumes(popupId);

        Set<Long> allItemIds = collectAllItemIds(popularityMap, salesMap);

        if (allItemIds.isEmpty()) {
            return Collections.emptyList();
        }

        updateItemAnalysis(allItemIds, popularityMap, salesMap);

        List<ItemAnalysis> topItems = findTopItems(popupId);

        if (topItems.isEmpty()) {
            return Collections.emptyList();
        }

        return convertToResponse(topItems);
    }

    private Popup findPopupById(Long popupId) {
        return popupRepository
                .findById(popupId)
                .orElseThrow(() -> new CustomException(PopupErrorCode.POPUP_NOT_FOUND));
    }

    private void validatePopupOwnership(Manager manager, Popup popup) {
        if (!popup.getManager().equals(manager)) {
            throw new CustomException(PopupErrorCode.POPUP_UNAUTHORIZED);
        }
    }

    private LocalDateTime calculatePreviousTimeEnd(LocalDateTime currentTime) {
        int hour = currentTime.getHour();

        // 오전 6시부터 8시 사이인 경우, 직전 타임은 전날 22:00~23:59:59
        if (hour >= 6 && hour < 8) {
            return LocalDateTime.of(
                    currentTime.toLocalDate().minusDays(1), LocalTime.of(23, 59, 59));
        } else {
            // 직전 타임 계산 (2시간 간격)
            int previousTimeBlockEnd = ((hour - 6) / 2) * 2 + 6;
            return currentTime
                    .withHour(previousTimeBlockEnd)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);
        }
    }

    // 팝업의 아이템별 인기도 점수 조회
    private Map<Long, Integer> getItemPopularityScores(Long popupId, LocalDateTime endTime) {
        List<PopupEventResponse> events = dynamoDBRepository.getEventsUntilTime(popupId, endTime);
        return events.stream()
                .filter(event -> event.getItemId() != null && event.getScore() > 0)
                .collect(
                        Collectors.groupingBy(
                                PopupEventResponse::getItemId,
                                Collectors.summingInt(PopupEventResponse::getScore)));
    }

    // 팝업의 아이템별 판매량 조회
    private Map<Long, Integer> getItemSalesVolumes(Long popupId) {
        List<ItemSalesStats> itemSalesStats = itemSalesStatsRepository.findByPopupId(popupId);
        return itemSalesStats.stream()
                .collect(
                        Collectors.toMap(
                                ItemSalesStats::getItemId,
                                ItemSalesStats::getSalesVolume,
                                (v1, v2) -> v1));
    }

    private Set<Long> collectAllItemIds(
            Map<Long, Integer> popularityMap, Map<Long, Integer> salesMap) {
        Set<Long> allItemIds = new HashSet<>();
        allItemIds.addAll(popularityMap.keySet());
        allItemIds.addAll(salesMap.keySet());
        return allItemIds;
    }

    private void updateItemAnalysis(
            Set<Long> itemIds, Map<Long, Integer> popularityMap, Map<Long, Integer> salesMap) {
        for (Long itemId : itemIds) {
            Item item = itemRepository.findById(itemId).orElse(null);
            if (item == null) continue;

            int popularityScore = popularityMap.getOrDefault(itemId, 0);
            int salesVolume = salesMap.getOrDefault(itemId, 0);

            ItemAnalysis analysis =
                    itemAnalysisRepository
                            .findByItemId(itemId)
                            .orElse(ItemAnalysis.createItemAnalysis(item, 0, 0.0, 0));

            analysis.updateScores(popularityScore, salesVolume);
            itemAnalysisRepository.save(analysis);
        }
    }

    private List<ItemAnalysis> findTopItems(Long popupId) {
        return itemAnalysisRepository.findTop3ItemsByPopupId(popupId);
    }

    private List<ItemTrendingResponse> convertToResponse(List<ItemAnalysis> topItems) {
        return topItems.stream()
                .map(analysis -> ItemTrendingResponse.from(analysis.getItem()))
                .collect(Collectors.toList());
    }
}
