package com.lgcns.domain.itemAnalysis.service;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.itemAnalysis.domain.ItemAnalysis;
import com.lgcns.domain.itemAnalysis.domain.ItemSalesStats;
import com.lgcns.domain.itemAnalysis.dto.ItemScore;
import com.lgcns.domain.itemAnalysis.dto.response.ItemTrendingResponse;
import com.lgcns.domain.itemAnalysis.repository.ItemAnalysisRepository;
import com.lgcns.domain.itemAnalysis.repository.ItemSalesStatsRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import com.lgcns.infra.dynamodb.itemAnalysis.PopupEventDynamoDbClient;
import com.lgcns.infra.dynamodb.itemAnalysis.PopupEventResponse;
import java.time.LocalDate;
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

    private final PopupEventDynamoDbClient dynamoDbClient;
    private final ItemSalesStatsRepository itemSalesStatsRepository;
    private final ItemRepository itemRepository;
    private final PopupRepository popupRepository;
    private final ManagerUtil managerUtil;
    private final ItemAnalysisRepository itemAnalysisRepository;

    private final int DASHBOARD_HOT_ITEM_SIZE = 3;

    @Override
    public List<ItemTrendingResponse> getTrendingItems(Long popupId) {
        final Manager currentManager = managerUtil.getCurrentManager();
        final Popup popup = findPopupById(popupId);

        validatePopupOwnership(currentManager, popup);

        List<ItemAnalysis> topItems =
                itemAnalysisRepository.findTopItemsByPopupId(popupId, DASHBOARD_HOT_ITEM_SIZE);

        return topItems.isEmpty() ? Collections.emptyList() : convertToResponse(topItems);
    }

    @Override
    public List<Long> findTargetPopupIds() {
        List<Popup> activePopups = findActivePopups();
        return activePopups.stream().map(Popup::getId).toList();
    }

    @Override
    public List<ItemAnalysis> processPopupItemAnalysis(Long popupId) {
        LocalDateTime endTime = calculatePreviousTimeEnd(LocalDateTime.now());
        List<ItemScore> itemScoreList = getItemScoreList(popupId, endTime);

        if (itemScoreList.isEmpty()) {
            return Collections.emptyList();
        }

        List<ItemAnalysis> itemAnalysisList = new ArrayList<>();
        for (ItemScore itemScore : itemScoreList) {
            ItemAnalysis itemAnalysis = createOrUpdateItemAnalysis(itemScore);
            if (itemAnalysis != null) {
                itemAnalysisList.add(itemAnalysis);
            }
        }

        return itemAnalysisList;
    }

    @Override
    public void saveItemAnalysisList(List<ItemAnalysis> itemAnalysisList) {
        List<ItemAnalysis> validItems = itemAnalysisList.stream().filter(Objects::nonNull).toList();

        if (!validItems.isEmpty()) {
            itemAnalysisRepository.bulkInsertOrUpdate(validItems);
        }
    }

    private List<Popup> findActivePopups() {
        return popupRepository.findAll().stream()
                .filter(popup -> popup.getPopupEndDate().isAfter(LocalDate.now().minusDays(1)))
                .toList();
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

        if (hour >= 6 && hour < 7) {
            return LocalDateTime.of(
                    currentTime.toLocalDate().minusDays(1), LocalTime.of(23, 59, 59));
        } else {
            int previousTimeBlockEnd = (hour - 1 + 24) % 24;
            return currentTime
                    .withHour(previousTimeBlockEnd)
                    .withMinute(59)
                    .withSecond(59)
                    .withNano(0);
        }
    }

    private List<ItemScore> getItemScoreList(Long popupId, LocalDateTime endTime) {
        List<Item> itemList = itemRepository.findItemsByPopupId(popupId);

        List<ItemAnalysis> existingAnalysisList = itemAnalysisRepository.findAllByPopupId(popupId);
        Map<Long, ItemAnalysis> analysisMap =
                existingAnalysisList.stream()
                        .collect(
                                Collectors.toMap(
                                        analysis -> analysis.getItem().getId(),
                                        analysis -> analysis));

        Set<Long> allItemIds = new HashSet<>();
        Map<Long, Integer> popularityScoreMap = new HashMap<>();

        List<PopupEventResponse> allEvents =
                dynamoDbClient.getEventsBetweenTimes(popupId, null, endTime);

        Map<Long, List<PopupEventResponse>> groupedEvents =
                allEvents.stream()
                        .filter(e -> e.getItemId() != null && e.getScore() > 0)
                        .collect(Collectors.groupingBy(PopupEventResponse::getItemId));

        // 인기도 점수 계산 (DynamoDB)
        for (Item item : itemList) {
            Long itemId = item.getId();
            allItemIds.add(itemId);

            ItemAnalysis analysis = analysisMap.get(itemId);

            List<PopupEventResponse> itemEvents =
                    groupedEvents.getOrDefault(itemId, Collections.emptyList());

            int additionalScore = itemEvents.stream().mapToInt(PopupEventResponse::getScore).sum();

            int previousScore = analysis != null ? analysis.getPopularityScore() : 0;
            int totalScore = previousScore + additionalScore;
            popularityScoreMap.put(itemId, totalScore);
        }

        Map<Long, Integer> salesVolumeMap = new HashMap<>();
        List<ItemSalesStats> salesStats = itemSalesStatsRepository.findByPopupId(popupId);
        for (ItemSalesStats stat : salesStats) {
            Long itemId = stat.getItemId();
            allItemIds.add(itemId);
            salesVolumeMap.put(itemId, stat.getSalesVolume());
        }

        List<ItemScore> result = new ArrayList<>(allItemIds.size());
        for (Long itemId : allItemIds) {
            int popularityScore = popularityScoreMap.getOrDefault(itemId, 0);
            int salesVolume = salesVolumeMap.getOrDefault(itemId, 0);
            result.add(ItemScore.of(itemId, popularityScore, salesVolume));
        }

        return result;
    }

    private ItemAnalysis createOrUpdateItemAnalysis(ItemScore score) {
        Item item = itemRepository.findById(score.itemId()).orElse(null);
        if (item == null) return null;

        ItemAnalysis itemAnalysis =
                itemAnalysisRepository
                        .findByItemId(score.itemId())
                        .orElse(ItemAnalysis.createItemAnalysis(item));

        itemAnalysis.updateScores(score.popularityScore(), score.salesVolume());
        return itemAnalysis;
    }

    private List<ItemTrendingResponse> convertToResponse(List<ItemAnalysis> topItems) {
        return topItems.stream()
                .map(analysis -> ItemTrendingResponse.from(analysis.getItem()))
                .collect(Collectors.toList());
    }
}
