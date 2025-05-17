package com.lgcns.domain.conversionStats.service;

import com.lgcns.domain.conversionStats.domain.PopupEvent;
import com.lgcns.domain.conversionStats.dto.response.ConversionStatsResponse;
import com.lgcns.domain.conversionStats.repository.ConversionStatsRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import com.lgcns.infra.dynamodb.DynamoDbProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ConversionStatsServiceImpl implements ConversionStatsService {

    private final ManagerUtil managerUtil;
    private final ConversionStatsRepository conversionStatsRepository;
    private final PopupRepository popupRepository;
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbProperties dynamoDbProperties;

    @Override
    public ConversionStatsResponse findTopAndLowConversionItems(Long popupId) {
        final Manager currentManager = managerUtil.getCurrentManager();
        final Popup popup = findPopupById(popupId);

        validatePopupOwnership(currentManager, popup);

        return conversionStatsRepository.findTopAndLowConversionItems(popupId);
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
