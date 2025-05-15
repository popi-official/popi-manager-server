package com.lgcns.domain.itemAnalysis.repository;

import com.lgcns.domain.itemAnalysis.dto.response.PopupEventResponse;
import com.lgcns.infra.dynamodb.DynamoDbProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DynamoDBRepository {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbProperties dynamoDbProperties;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public List<PopupEventResponse> getEventsUntilTime(Long popupId, LocalDateTime endTime) {
        try {
            String endTimeStr = endTime.format(formatter);

            String endEventKeyPrefix = endTimeStr + "#";

            DynamoDbTable<PopupEventResponse> table = dynamoDbEnhancedClient.table(
                    dynamoDbProperties.tableName(),
                    TableSchema.fromBean(PopupEventResponse.class));

            QueryConditional keyCondition = QueryConditional.keyEqualTo(
                    Key.builder().partitionValue(popupId.toString()).build());

            Map<String, AttributeValue> expressionValues = new HashMap<>();
            expressionValues.put(":endEventKeyPrefix", AttributeValue.builder().s(endEventKeyPrefix).build());

            Map<String, String> expressionNames = new HashMap<>();
            expressionNames.put("#ek", "event_key");

            Expression filterExpression = Expression.builder()
                    .expression("#ek <= :endEventKeyPrefix")
                    .expressionNames(expressionNames)
                    .expressionValues(expressionValues)
                    .build();

            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                    .queryConditional(keyCondition)
                    .filterExpression(filterExpression)
                    .attributesToProject("popup_id", "event_key", "item_id", "score")
                    .build();

            // 쿼리
            return table.query(request)
                    .items()
                    .stream()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
