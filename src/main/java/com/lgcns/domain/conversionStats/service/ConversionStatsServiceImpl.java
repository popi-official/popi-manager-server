package com.lgcns.domain.conversionStats.service;

import com.lgcns.domain.conversionStats.domain.ConversionStats;
import com.lgcns.domain.conversionStats.domain.PopupEvent;
import com.lgcns.domain.conversionStats.dto.response.ConversionItem;
import com.lgcns.domain.conversionStats.dto.response.ConversionItemsResponse;
import com.lgcns.domain.conversionStats.dto.response.ItemBuyerCountResponse;
import com.lgcns.domain.conversionStats.repository.ConversionStatsRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import com.lgcns.infra.client.payment.PaymentServiceClient;
import com.lgcns.infra.dynamodb.DynamoDbProperties;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ConversionStatsServiceImpl implements ConversionStatsService {

    private final ManagerUtil managerUtil;
    private final PopupRepository popupRepository;
    private final ConversionStatsRepository conversionStatsRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbProperties dynamoDbProperties;

    @Override
    @Transactional(readOnly = true)
    public ConversionItemsResponse findConversionItems(Long popupId) {
        final Manager currentManager = managerUtil.getCurrentManager();
        final Popup popup = findPopupById(popupId);
        validatePopupOwnership(currentManager, popup);

        List<ConversionItem> low =
                conversionStatsRepository.findTop6LowConversionItemsByPopupId(popupId);
        List<ConversionItem> high =
                conversionStatsRepository.findTop6HighConversionItemsByPopupId(popupId);

        return new ConversionItemsResponse(low, high);
    }

    @Override
    public void createConversionStats() {
        List<Long> popupIds = popupRepository.findAllPopupIds();

        List<ConversionStats> statsList = new ArrayList<>();

        for (Long popupId : popupIds) {
            try {
                List<ItemBuyerCountResponse> counts =
                        paymentServiceClient.countItemBuyerByPopupId(popupId);

                for (ItemBuyerCountResponse count : counts) {
                    int interestedCount =
                            countInterestedUsersByItem(popupId, count.itemId()).intValue();
                    int buyerCount = count.buyerCount();
                    int conversionRate = count.buyerCount() / interestedCount * 100;

                    ConversionStats stats =
                            ConversionStats.createConversionStats(
                                    popupId,
                                    count.itemId(),
                                    interestedCount,
                                    buyerCount,
                                    conversionRate,
                                    LocalDate.now(),
                                    LocalTime.now());

                    statsList.add(stats);
                }

            } catch (Exception e) {
                log.warn("Failed to prepare conversion stats for popupId: {}", popupId, e);
            }
        }

        if (!statsList.isEmpty()) {
            conversionStatsRepository.bulkInsertConversionStats(statsList);
        }
    }

    private Long countInterestedUsersByItem(Long popupId, Long targetItemId) {
        DynamoDbTable<PopupEvent> table =
                dynamoDbEnhancedClient.table(
                        dynamoDbProperties.tableName(), TableSchema.fromBean(PopupEvent.class));

        QueryConditional condition =
                QueryConditional.keyEqualTo(
                        Key.builder().partitionValue(popupId.toString()).build());

        return table.query(r -> r.queryConditional(condition)).items().stream()
                .filter(i -> i.getItemId().equals(targetItemId))
                .count();
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
}
