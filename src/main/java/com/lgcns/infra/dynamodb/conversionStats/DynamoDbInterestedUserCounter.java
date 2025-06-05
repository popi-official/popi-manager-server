package com.lgcns.infra.dynamodb.conversionStats;

import com.lgcns.infra.dynamodb.DynamoDbProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Component
@RequiredArgsConstructor
public class DynamoDbInterestedUserCounter {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbProperties dynamoDbProperties;

    public long countInterestedUsers(Long popupId, Long targetItemId) {
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
}
