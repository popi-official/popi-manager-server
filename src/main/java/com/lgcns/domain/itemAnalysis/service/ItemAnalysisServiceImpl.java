package com.lgcns.domain.itemAnalysis.service;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.itemAnalysis.domain.ItemSalesStats;
import com.lgcns.domain.itemAnalysis.dto.response.ItemScoreResponse;
import com.lgcns.domain.itemAnalysis.dto.response.ItemTrendingResponse;
import com.lgcns.domain.itemAnalysis.dto.response.PopupEventResponse;
import com.lgcns.domain.itemAnalysis.repository.DynamoDBRepository;
import com.lgcns.domain.itemAnalysis.repository.ItemSalesStatsRepository;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
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

    @Override
    @Transactional(readOnly = true)
    public List<ItemTrendingResponse> getTrendingItems(Long popupId) {
        // 팝업 존재 확인
        validatePopupExists(popupId);

        // 현재 시간 기준으로 직전 타임 계산
        LocalDateTime endTime = calculatePreviousTimeEnd(LocalDateTime.now());

        // 인기도와 판매량 데이터 수집
        Map<Long, Integer> popularityMap = getPopularityMap(popupId, endTime);
        Map<Long, Integer> salesMap = getSalesMap(popupId);

        // 인기 상품 TOP 3 조회
        List<ItemScoreResponse> topItems = findTopItems(popularityMap, salesMap);

        // 상품 정보 조회 및 응답 생성
        return createTrendingResponses(topItems);
    }

    private void validatePopupExists(Long popupId) {
        popupRepository
                .findById(popupId)
                .orElseThrow(() -> new CustomException(PopupErrorCode.POPUP_NOT_FOUND));
    }

    private LocalDateTime calculatePreviousTimeEnd(LocalDateTime currentTime) {
        int hour = currentTime.getHour();

        // 오전 6시부터 8시 사이인 경우, 직전 타임은 전날 22:00~24:00
        if (hour >= 6 && hour < 8) {
            return LocalDateTime.of(currentTime.toLocalDate(), LocalTime.of(0, 0));
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

    private Map<Long, Integer> getPopularityMap(Long popupId, LocalDateTime endTime) {
        // DynamoDB에서 이벤트 데이터 조회 및 인기도 점수 계산
        return dynamoDBRepository.getEventsUntilTime(popupId, endTime).stream()
                .collect(
                        Collectors.groupingBy(
                                PopupEventResponse::getItemId,
                                Collectors.summingInt(PopupEventResponse::getScore)));
    }

    private Map<Long, Integer> getSalesMap(Long popupId) {
        // 판매량 데이터 조회
        return itemSalesStatsRepository.findByPopupId(popupId).stream()
                .collect(
                        Collectors.toMap(
                                ItemSalesStats::getItemId,
                                ItemSalesStats::getSalesVolume,
                                (v1, v2) -> v1 // 중복 키가 있을 경우 첫 번째 값 사용
                                ));
    }

    private List<ItemScoreResponse> findTopItems(
            Map<Long, Integer> popularityMap, Map<Long, Integer> salesMap) {
        // 모든 아이템 ID 목록 생성
        Set<Long> allItemIds = new HashSet<>();
        allItemIds.addAll(popularityMap.keySet());
        allItemIds.addAll(salesMap.keySet());

        // 인기도 + 판매량을 기준으로 상위 3개 아이템 선정
        return allItemIds.stream()
                .map(
                        itemId ->
                                ItemScoreResponse.of(
                                        itemId,
                                        popularityMap.getOrDefault(itemId, 0),
                                        salesMap.getOrDefault(itemId, 0)))
                .sorted(
                        Comparator.comparingInt(ItemScoreResponse::totalScore)
                                .reversed()
                                .thenComparingInt(ItemScoreResponse::salesVolume)
                                .reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    private List<ItemTrendingResponse> createTrendingResponses(List<ItemScoreResponse> topItems) {
        if (topItems.isEmpty()) {
            return Collections.emptyList();
        }

        // 상품 ID 목록 추출
        List<Long> topItemIds =
                topItems.stream().map(ItemScoreResponse::itemId).collect(Collectors.toList());

        // 상품 정보 조회
        Map<Long, Item> itemMap =
                itemRepository.findAllById(topItemIds).stream()
                        .collect(Collectors.toMap(Item::getId, Function.identity()));

        // 원래 순서(점수 순)대로 응답 생성
        return topItems.stream()
                .map(
                        scoreItem ->
                                Optional.ofNullable(itemMap.get(scoreItem.itemId()))
                                        .map(ItemTrendingResponse::from)
                                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
