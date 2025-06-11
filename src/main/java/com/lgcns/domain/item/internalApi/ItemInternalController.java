package com.lgcns.domain.item.internalApi;

import com.lgcns.domain.item.client.dto.request.ItemIdsForPaymentRequest;
import com.lgcns.domain.item.client.dto.response.ItemForPaymentResponse;
import com.lgcns.domain.item.client.dto.response.ItemInfoResponse;
import com.lgcns.domain.item.service.ItemService;
import com.lgcns.global.common.response.SliceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/popups/{popupId}/items")
@RequiredArgsConstructor
@Tag(name = "04-2. 상품 Internal API", description = "상품 관련 Internal API 입니다.")
public class ItemInternalController {

    private final ItemService itemService;

    @GetMapping
    @Operation(
            summary = "상품 목록 조회",
            description =
                    "keyword를 포함하지 않으면 현재 팝업의 모든 상품을 무한 스크롤을 위하여 페이징 처리한 뒤 반환합니다.</br>"
                            + "keyword를 포함하여 호출하면 상품명에 keyword가 포함된 모든 상품을 페이징 처리한 뒤 반환합니다.")
    public SliceResponse<ItemInfoResponse> itemFindByName(
            @Parameter(description = "팝업 ID", example = "1") @PathVariable(name = "popupId")
                    Long popupId,
            @Parameter(description = "검색할 상품 이름 (비워두면 모든 상품을 반환합니다.)", example = "블랙핑크")
                    @RequestParam(name = "keyword", required = false)
                    String keyword,
            @Parameter(description = "이전 페이지의 마지막 ID (첫 요청 시 비워두세요.)", example = "2")
                    @RequestParam(name = "lastItemId", required = false)
                    Long lastItemId,
            @Parameter(description = "페이지 크기 (기본 8)", example = "8")
                    @RequestParam(name = "size", defaultValue = "8")
                    int size) {
        return itemService.findItemsByNameWithPagination(popupId, keyword, lastItemId, size);
    }

    @GetMapping("/default")
    @Operation(summary = "기본 상품 목록 조회", description = "무작위하게 선택된 4개의 상품을 조회합니다.")
    public List<ItemInfoResponse> itemFindDefault(
            @Parameter(description = "팝업 ID", example = "1") @PathVariable(name = "popupId")
                    Long popupId) {
        return itemService.findRandomItems(popupId);
    }

    @PostMapping("/details")
    @Operation(
            summary = "결제용 상품 정보 조회",
            description = "사용자가 장바구니에서 선택한 상품 ID 목록을 기반으로, 해당 팝업에 등록된 아이템들의 이름, 가격, 재고 정보를 반환합니다.")
    public List<ItemForPaymentResponse> findItemDetail(
            @Parameter(description = "팝업 ID", example = "1") @PathVariable(name = "popupId")
                    Long popupId,
            @RequestBody ItemIdsForPaymentRequest request) {
        return itemService.findItemsForPayment(popupId, request);
    }

    @GetMapping("/popularity")
    @Operation(summary = "인기 상품 목록 조회", description = "카메라 점수 및 실구매율 기반 인기 상품 3개를 조회합니다.")
    public List<ItemInfoResponse> findItemPopularity(
            @Parameter(description = "팝업 ID", example = "1") @PathVariable(name = "popupId")
                    Long popupId) {
        return itemService.getTrendingItemsByUser(popupId);
    }
}
