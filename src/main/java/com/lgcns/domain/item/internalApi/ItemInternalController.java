package com.lgcns.domain.item.internalApi;

import com.lgcns.domain.item.client.dto.ItemInfoResponse;
import com.lgcns.domain.item.service.ItemService;
import com.lgcns.global.common.response.SliceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/popups/{popupId}/items")
@RequiredArgsConstructor
@Tag(name = "4-2. 상품 Internal API", description = "상품 관련 Internal API 입니다.")
public class ItemInternalController {

    private final ItemService itemService;

    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "현재 팝업의 모든 상품을 무한 스크롤을 위하여 페이징 처리한 뒤 반환합니다.")
    public SliceResponse<ItemInfoResponse> userItemFindAll(
            @PathVariable(name = "popupId") Long popupId,
            @RequestParam(name = "lastItemId", required = false) Long lastItemId,
            @RequestParam(name = "size", defaultValue = "8") int size) {
        return itemService.findAllItemsByPagination(popupId, lastItemId, size);
    }
}
