package com.lgcns.domain.itemAnalysis.dto.response;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class PopupEventResponse {
    private String popupId;
    private String eventKey;
    private Long itemId;
    private int score;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("popup_id")
    public String getPopupId() {
        return popupId;
    }

    public void setPopupId(String popupId) {
        this.popupId = popupId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("event_key")
    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    @DynamoDbAttribute("item_id")
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    @DynamoDbAttribute("score")
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
