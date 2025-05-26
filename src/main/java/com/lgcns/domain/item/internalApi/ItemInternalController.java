package com.lgcns.domain.item.internalApi;

import com.lgcns.domain.item.client.dto.ItemInfoResponse;
import com.lgcns.domain.item.service.ItemService;
import com.lgcns.global.common.response.SliceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(
            summary = "상품 목록 조회",
            description =
                    "searchName을 포함하지 않으면 현재 팝업의 모든 상품을 무한 스크롤을 위하여 페이징 처리한 뒤 반환합니다.</br>"
                            + "searchName을 포함하여 호출하면 상품명에 searchName이 포함된 모든 상품을 페이징 처리한 뒤 반환합니다.")
    public SliceResponse<ItemInfoResponse> userItemFindAll(
            @Parameter(description = "팝업 ID", example = "1") @PathVariable(name = "popupId")
                    Long popupId,
            @Parameter(description = "검색할 상품 이름 (비워두면 모든 상품을 반환합니다.)", example = "블랙핑크")
                    @RequestParam(name = "searchName", required = false)
                    String searchName,
            @Parameter(description = "이전 페이지의 마지막 ID (첫 요청 시 비워두세요.)", example = "2")
                    @RequestParam(name = "lastItemId", required = false)
                    Long lastItemId,
            @Parameter(description = "페이지 크기 (기본 8)", example = "8")
                    @RequestParam(name = "size", defaultValue = "8")
                    int size) {
        return itemService.findItemsByNameWithPagination(popupId, searchName, lastItemId, size);
    }
}
