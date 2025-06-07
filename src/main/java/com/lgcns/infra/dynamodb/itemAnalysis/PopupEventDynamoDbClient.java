package com.lgcns.infra.dynamodb.itemAnalysis;

import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.error.exception.GlobalErrorCode;
import com.lgcns.infra.dynamodb.DynamoDbProperties;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PopupEventDynamoDbClient {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbProperties dynamoDbProperties;
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final LocalDateTime EARLIEST_DATE = LocalDateTime.of(2000, 1, 1, 0, 0, 0);

    public List<PopupEventResponse> getEventsBetweenTimes(
            Long popupId, LocalDateTime startTime, LocalDateTime endTime) {
        String startTimeStr =
                startTime != null ? startTime.format(formatter) : EARLIEST_DATE.format(formatter);

        String endTimeStr = endTime.format(formatter);

        try {
            DynamoDbTable<PopupEventResponse> table =
                    dynamoDbEnhancedClient.table(
                            dynamoDbProperties.tableName(),
                            TableSchema.fromBean(PopupEventResponse.class));

            QueryConditional keyCondition =
                    QueryConditional.keyEqualTo(
                            Key.builder().partitionValue(popupId.toString()).build());

            QueryEnhancedRequest request =
                    QueryEnhancedRequest.builder()
                            .queryConditional(keyCondition)
                            .scanIndexForward(true)
                            .build();

            return table.query(request).items().stream()
                    .filter(
                            event -> {
                                String eventTime = event.getEventKey().split("#")[0];
                                return eventTime.compareTo(startTimeStr) >= 0
                                        && eventTime.compareTo(endTimeStr) <= 0;
                            })
                    .collect(Collectors.toList());

        } catch (ResourceNotFoundException e) {
            log.error("DynamoDB table not found: {}", dynamoDbProperties.tableName(), e);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error(
                    "Unexpected error fetching events for popup {} until time {}: {}",
                    popupId,
                    startTimeStr,
                    endTimeStr,
                    e.getMessage(),
                    e);
            throw new CustomException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
