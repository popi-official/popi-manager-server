package com.lgcns.domain.orderItem.dto.response;

import com.lgcns.domain.orderItem.domian.OrderItemStatus;
import java.time.LocalDateTime;

public record OrderItemResponse(
        Long orderItemId,
        String itemName,
        Integer recommendCount,
        Integer realCount,
        LocalDateTime lastRestockDateTime,
        OrderItemStatus status) {}
