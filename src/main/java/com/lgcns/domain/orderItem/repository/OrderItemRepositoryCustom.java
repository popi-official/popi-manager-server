package com.lgcns.domain.orderItem.repository;

import com.lgcns.domain.orderItem.dto.response.OrderItemResponse;
import org.springframework.data.domain.Slice;

public interface OrderItemRepositoryCustom {
    Slice<OrderItemResponse> findOrderItemsByPopupIdWithPagination(
            Long popupId, Long lastOrderItemId, int size);
}
