package com.lgcns.domain.orderItem.dto.response;

import com.lgcns.domain.orderItem.domian.OrderItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record OrderItemResponse(
        @Schema(description = "발주 ID", example = "1") Long orderItemId,
        @Schema(description = "상품명", example = "블랙핑크 키링") String itemName,
        @Schema(description = "추천 발주 수량", example = "10") Integer recommendCount,
        @Schema(description = "실제 발주 수량", example = "8") Integer realCount,
        @Schema(description = "발주 날짜", example = "2023-10-01T10:00:00")
                LocalDateTime lastRestockDateTime,
        @Schema(description = "발주 상태", example = "PENDING") OrderItemStatus status) {}
