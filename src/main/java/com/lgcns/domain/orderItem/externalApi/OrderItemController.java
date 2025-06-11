package com.lgcns.domain.orderItem.externalApi;

import com.lgcns.domain.orderItem.dto.request.OrderItemUpdateRequest;
import com.lgcns.domain.orderItem.dto.response.OrderItemResponse;
import com.lgcns.domain.orderItem.service.OrderItemService;
import com.lgcns.global.common.response.SliceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order-items")
@RequiredArgsConstructor
@Tag(name = "08. 발주 API", description = "발주 관련 External API 입니다.")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping("/{popupId}")
    @Operation(summary = "팝업 상품에 대한 발주 조회", description = "주어진 팝업 ID에 대한 발주 내역을 페이징 처리하여 조회합니다.")
    public SliceResponse<OrderItemResponse> orderItemFindAll(
            @PathVariable Long popupId,
            @Parameter(description = "이전 페이지의 마지막 ID (첫 요청 시 비워두세요.)", example = "2")
                    @RequestParam(required = false)
                    Long lastOrderItemId,
            @Parameter(description = "페이지 크기 (기본 8)", example = "8")
                    @RequestParam(defaultValue = "8")
                    int size) {
        return orderItemService.findOrderItemsByPopupId(popupId, lastOrderItemId, size);
    }

    @PatchMapping("/status/{orderItemId}")
    @Operation(summary = "발주 상태 변경", description = "주어진 발주 ID에 대한 발주 상태를 변경합니다.")
    public ResponseEntity<Void> orderItemUpdate(
            @PathVariable Long orderItemId,
            @Valid @RequestBody OrderItemUpdateRequest orderItemUpdateRequest) {
        orderItemService.updateOrderItem(orderItemId, orderItemUpdateRequest);
        return ResponseEntity.ok().build();
    }
}
