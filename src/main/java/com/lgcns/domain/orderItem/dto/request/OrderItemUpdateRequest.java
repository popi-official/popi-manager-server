package com.lgcns.domain.orderItem.dto.request;

import com.lgcns.domain.orderItem.domian.OrderItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record OrderItemUpdateRequest(
        @Schema(description = "발주 수량", example = "10") Integer qty,
        @Schema(description = "발주 상태", example = "CANCELED") @NotNull OrderItemStatus status) {}
