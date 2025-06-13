package com.lgcns.domain.item.service;

import com.lgcns.domain.item.client.dto.request.ItemIdsForPaymentRequest;
import com.lgcns.domain.item.client.dto.response.ItemForPaymentResponse;
import com.lgcns.domain.item.client.dto.response.ItemInfoResponse;
import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.dto.ItemScore;
import com.lgcns.domain.item.dto.request.ItemCreateRequest;
import com.lgcns.domain.item.dto.request.ItemMinStockUpdateRequest;
import com.lgcns.domain.item.dto.response.*;
import com.lgcns.domain.item.exception.ItemErrorCode;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.item.util.ItemExcelUtil;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.common.response.SliceResponse;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import com.lgcns.infra.dynamodb.itemAnalysis.PopupEventDynamoDbClient;
import com.lgcns.infra.dynamodb.itemAnalysis.PopupEventResponse;
import com.querydsl.core.Tuple;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private static final int HOT_ITEMS_LIMIT = 3;
    private final ItemRepository itemRepository;
    private final PopupRepository popupRepository;
    private final ManagerUtil managerUtil;
    private final PopupEventDynamoDbClient dynamoDbClient;

    private final int DASHBOARD_HOT_ITEM_SIZE = 3;
    private final View error;

    @Override
    public void createItem(Long popupId, ItemCreateRequest request) {
        final Manager currentManager = managerUtil.getCurrentManager();
        final Popup popup = findPopupById(popupId);

        validatePopupOwnership(currentManager, popup);

        Item item =
                Item.createItem(
                        popup,
                        request.name(),
                        request.imageUrl(),
                        request.price(),
                        request.stock(),
                        request.minStock(),
                        request.location());

        itemRepository.save(item);
    }

    @Override
    public ItemBulkCreateResponse createItemByExcel(Long popupId, MultipartFile itemFile) {

        ItemExcelUtil.validateExcelFile(itemFile);

        final Manager currentManager = managerUtil.getCurrentManager();
        final Popup popup = findPopupById(popupId);
        validatePopupOwnership(currentManager, popup);

        Sheet sheet = openExcelSheet(itemFile);
        ItemExcelUtil.validateSheet(sheet);

        List<Item> validItems = processExcelData(popup, sheet);
        saveItems(validItems, popupId);

        return ItemBulkCreateResponse.success(validItems.size());
    }

    @Override
    public Map<String, List<ItemPreviewResponse>> findAllItems(Long popupId) {
        List<ItemLocationResponse> projections = itemRepository.findItemsWithSplitLocation(popupId);

        return groupItemsByLocation(projections);
    }

    @Override
    public ItemDetailResponse updateItemMinStock(
            Long popupId, Long itemId, ItemMinStockUpdateRequest request) {
        final Manager currentManager = managerUtil.getCurrentManager();
        final Popup popup = findPopupById(popupId);
        validatePopupOwnership(currentManager, popup);

        final Item item = findByItemId(itemId);
        validateItemBelongsToPopup(item, popupId);

        validateMinStock(item, request.minStock());

        item.updateMinStock(request.minStock());

        return ItemDetailResponse.from(item);
    }

    @Override
    public void deleteItem(Long popupId, Long itemId) {
        final Manager currentManager = managerUtil.getCurrentManager();

        final Popup popup = findPopupById(popupId);
        validatePopupOwnership(currentManager, popup);

        final Item item = findByItemId(itemId);
        validateItemBelongsToPopup(item, popupId);

        itemRepository.delete(item);
    }

    @Override
    @Transactional(readOnly = true)
    public SliceResponse<ItemInfoResponse> findItemsByNameWithPagination(
            Long popupId, String keyword, Long lastItemId, int size) {
        Slice<ItemInfoResponse> itemInfoResponses =
                itemRepository.findItemsByNameWithPagination(popupId, keyword, lastItemId, size);

        return SliceResponse.from(itemInfoResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemInfoResponse> findRandomItems(Long popupId) {
        return itemRepository.findRandomItems(popupId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemForPaymentResponse> findItemsForPayment(
            Long popupId, ItemIdsForPaymentRequest request) {
        return itemRepository.findItemsForPayment(popupId, request.itemIds());
    }

    @Override
    public void updateItemAverageSales() {
        itemRepository.bulkUpdateAverageSalesForOperatingPopups();
    }

    @Override
    public void updateItemRecommendCount() {
        LocalDate today = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        List<Tuple> allPopupIdsAndRemainingDays =
                popupRepository.findAllPopupIdsAndRemainingDays(today.minusDays(1), nowTime);

        for (Tuple tuple : allPopupIdsAndRemainingDays) {
            Long popupId = tuple.get(0, Long.class);
            LocalDate popupEndDate = Objects.requireNonNull(tuple.get(1, LocalDate.class));
            int remainingDays = (int) ChronoUnit.DAYS.between(today, popupEndDate);

            List<Item> items = itemRepository.findAllByPopupId(popupId);

            int maxPopularity = items.stream().mapToInt(Item::getPopularityScore).max().orElse(1);

            for (Item item : items) {
                double normalizedPopularity =
                        (maxPopularity == 0)
                                ? 0.0
                                : (double) item.getPopularityScore() / maxPopularity;
                int avgSales = item.getAverageSales();

                int currentRecommendCount = item.getRecommendCount();

                int expectedNeed = avgSales * remainingDays;
                int shortfall = expectedNeed - currentRecommendCount;

                if (shortfall < 0) {
                    double reduceFactor = normalizedPopularity * 0.3 + 0.7;
                    int adjustedRecommend =
                            (int) Math.max(Math.round(expectedNeed * reduceFactor), 1);
                    item.updateRecommendCount(adjustedRecommend);
                } else {
                    double increaseFactor = normalizedPopularity * 0.3 + 1.0;
                    int adjustedRecommend =
                            (int) Math.max(Math.round(expectedNeed * increaseFactor), 1);
                    item.updateRecommendCount(adjustedRecommend);
                }
            }
            itemRepository.saveAll(items);
        }
    }

    @Override
    public List<ItemTrendingResponse> getTrendingItemsByManager(Long popupId) {
        final Manager currentManager = managerUtil.getCurrentManager();
        final Popup popup = findPopupById(popupId);

        validatePopupOwnership(currentManager, popup);

        List<Item> topItems =
                itemRepository.findTopItemsByPopupId(popupId, DASHBOARD_HOT_ITEM_SIZE);

        return topItems.isEmpty() ? Collections.emptyList() : convertToResponse(topItems);
    }

    @Override
    public List<ItemInfoResponse> getTrendingItemsByUser(Long popupId) {
        findPopupById(popupId);

        List<Item> topItems =
                itemRepository.findTopItemsByPopupId(popupId, DASHBOARD_HOT_ITEM_SIZE);

        return topItems.stream()
                .map(
                        item ->
                                ItemInfoResponse.of(
                                        item.getId(),
                                        item.getName(),
                                        item.getImageUrl(),
                                        item.getPrice()))
                .toList();
    }

    @Override
    public List<Long> findTargetPopupIds() {
        List<Popup> activePopups = findActivePopups();
        return activePopups.stream().map(Popup::getId).toList();
    }

    @Override
    public List<Item> processPopupItemAnalysis(Long popupId) {
        LocalDateTime endTime = calculatePreviousTimeEnd(LocalDateTime.now());
        List<ItemScore> itemScoreList = getItemScoreList(popupId, endTime);

        if (itemScoreList.isEmpty()) return Collections.emptyList();

        List<Item> itemList = new ArrayList<>();
        for (ItemScore itemScore : itemScoreList) {
            Item item = itemScore.item();
            item.updatePopularityScore(itemScore.popularityScore());
            itemList.add(item);
        }

        return itemList;
    }

    @Override
    public void updateItemAnalysisList(List<Item> itemList) {
        List<Item> validItems = itemList.stream().filter(Objects::nonNull).toList();

        if (!validItems.isEmpty()) {
            itemRepository.bulkUpdate(validItems);
        }
    }

    private Sheet openExcelSheet(MultipartFile itemFile) {
        try {
            // 파일 확장자 확인
            String originalFilename = itemFile.getOriginalFilename();
            if (originalFilename == null) {
                throw new CustomException(ItemErrorCode.EXCEL_FILE_INVALID);
            }

            InputStream inputStream = itemFile.getInputStream();

            if (originalFilename.toLowerCase().endsWith(".xlsx")) {
                // .xlsx 파일 처리
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                return workbook.getSheetAt(0);
            } else if (originalFilename.toLowerCase().endsWith(".xls")) {
                // .xls 파일 처리
                HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
                return workbook.getSheetAt(0);
            } else {
                throw new CustomException(ItemErrorCode.EXCEL_FILE_INVALID);
            }

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ItemErrorCode.EXCEL_PROCESSING_FAILED);
        }
    }

    private List<Item> processExcelData(Popup popup, Sheet sheet) {
        int totalRows = sheet.getPhysicalNumberOfRows();
        List<Item> validItems = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        int processedRows = 0;

        for (int rowIdx = 1; rowIdx < totalRows; rowIdx++) {
            Row row = sheet.getRow(rowIdx);

            if (ItemExcelUtil.isEmptyRow(row)) {
                continue;
            }

            processedRows++;

            try {
                Item item = ItemExcelUtil.parseRowToItem(popup, row, rowIdx);
                validItems.add(item);
            } catch (Exception e) {
                String errorMessage = String.format("Row %d: %s", rowIdx + 1, e.getMessage());
                errorMessages.add(errorMessage);
            }
        }

        validateProcessingResult(processedRows, errorMessages);

        return validItems;
    }

    private void validateProcessingResult(int processedRows, List<String> errorMessages) {
        if (processedRows == 0) {
            throw new CustomException(ItemErrorCode.EXCEL_DATA_INVALID);
        }

        if (!errorMessages.isEmpty()) {
            String combinedMessage = String.join("; ", errorMessages);

            throw new CustomException(ItemErrorCode.EXCEL_DATA_INVALID) {
                @Override
                public String getMessage() {
                    return super.getMessage() + " 상세: " + combinedMessage;
                }
            };
        }
    }

    private void saveItems(List<Item> validItems, Long popupId) {
        if (validItems.isEmpty()) {
            throw new CustomException(ItemErrorCode.EXCEL_DATA_INVALID);
        }

        try {
            itemRepository.saveAll(validItems);
        } catch (Exception e) {
            throw new CustomException(ItemErrorCode.EXCEL_PROCESSING_FAILED);
        }
    }

    private Map<String, List<ItemPreviewResponse>> groupItemsByLocation(
            List<ItemLocationResponse> projections) {
        return projections.stream()
                .collect(
                        Collectors.groupingBy(
                                ItemLocationResponse::locationGroup,
                                Collectors.mapping(
                                        ItemLocationResponse::toPreviewResponse,
                                        Collectors.toList())));
    }

    private void validatePopupOwnership(Manager manager, Popup popup) {
        if (!popup.getManager().equals(manager)) {
            throw new CustomException(PopupErrorCode.POPUP_UNAUTHORIZED);
        }
    }

    private void validateItemBelongsToPopup(Item item, Long popupId) {
        if (!item.getPopup().getId().equals(popupId)) {
            throw new CustomException(ItemErrorCode.ITEM_POPUP_MISMATCH);
        }
    }

    private Item findByItemId(Long itemId) {
        return itemRepository
                .findById(itemId)
                .orElseThrow(() -> new CustomException(ItemErrorCode.ITEM_NOT_FOUND));
    }

    private void validateMinStock(Item item, int minStock) {
        if (minStock > item.getStock()) {
            throw new CustomException(ItemErrorCode.MIN_STOCK_EXCEEDED);
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

    private LocalDateTime calculatePreviousTimeEnd(LocalDateTime currentTime) {
        int hour = currentTime.getHour();

        if (hour == 6) {
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
        List<Item> items = itemRepository.findAllByPopupId(popupId);

        Map<Long, List<PopupEventResponse>> eventsByItem = getGroupedPopupEvents(popupId, endTime);

        return items.stream()
                .map(item -> toItemScore(item, eventsByItem.get(item.getId())))
                .toList();
    }

    private Map<Long, List<PopupEventResponse>> getGroupedPopupEvents(
            Long popupId, LocalDateTime endTime) {
        return dynamoDbClient.getEventsBetweenTimes(popupId, null, endTime).stream()
                .filter(e -> e.getItemId() != null && e.getScore() > 0)
                .collect(Collectors.groupingBy(PopupEventResponse::getItemId));
    }

    private ItemScore toItemScore(Item item, List<PopupEventResponse> events) {
        int previousScore = item.getPopularityScore();
        int additionalScore =
                events != null ? events.stream().mapToInt(PopupEventResponse::getScore).sum() : 0;
        int totalScore = previousScore + additionalScore;

        return ItemScore.of(item, totalScore);
    }

    private List<ItemTrendingResponse> convertToResponse(List<Item> topItems) {
        return topItems.stream().map(ItemTrendingResponse::from).toList();
    }
}
