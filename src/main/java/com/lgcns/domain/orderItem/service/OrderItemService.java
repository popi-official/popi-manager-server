package com.lgcns.domain.orderItem.service;

import com.lgcns.domain.orderItem.dto.request.OrderItemUpdateRequest;
import com.lgcns.domain.orderItem.dto.response.OrderItemResponse;
import com.lgcns.global.common.response.SliceResponse;

public interface OrderItemService {

    void createOrderItem(Long itemId);

    SliceResponse<OrderItemResponse> findOrderItemsByPopupId(
            Long popupId, Long lastOrderItemId, int size);

    void updateOrderItem(Long orderItemId, OrderItemUpdateRequest orderItemUpdateRequest);
}
