package com.lgcns.domain.item.externalApi;

import com.lgcns.domain.item.dto.request.ItemCreateRequest;
import com.lgcns.domain.item.dto.request.ItemMinStockUpdateRequest;
import com.lgcns.domain.item.dto.response.ItemDetailResponse;
import com.lgcns.domain.item.dto.response.ItemPreviewResponse;
import com.lgcns.domain.item.dto.response.ItemTrendingResponse;
import com.lgcns.domain.item.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/popups/{popupId}")
@RequiredArgsConstructor
@Tag(name = "4-1. 상품 API", description = "상품 관련 API 입니다.")
public class ItemController {

    private final ItemService itemService;

    @PostMapping("/items")
    @Operation(summary = "단일 상품 등록", description = "단일 상품 정보를 모두 기입 한 후 등록합니다.")
    public ResponseEntity<Void> itemCreate(
            @PathVariable Long popupId, @Valid @RequestBody ItemCreateRequest request) {
        itemService.createItem(popupId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/items/excel")
    @Operation(summary = "excel 상품 파일 등록", description = "excel 파일을 업로드하여 상품 리스트를 등록합니다.")
    public ResponseEntity<Void> itemCreateByExcel(
            @PathVariable Long popupId, @RequestPart(value = "itemFile") MultipartFile itemFile)
            throws IOException, InvalidFormatException {
        itemService.createItemByExcel(popupId, itemFile);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/items")
    @Operation(summary = "전체 상품 조회", description = "해당 팝업에 등록된 모든 상품을 위치 그룹별 리스트로 반환합니다.")
    public Map<String, List<ItemPreviewResponse>> itemFindAll(@PathVariable Long popupId) {
        return itemService.findAllItems(popupId);
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "상품 삭제", description = "선택한 상품을 삭제합니다.")
    public ResponseEntity<Void> itemDelete(@PathVariable Long popupId, @PathVariable Long itemId) {
        itemService.deleteItem(popupId, itemId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/items/{itemId}")
    @Operation(summary = "상품 최소 발주 수량 수정", description = "선택한 상품의 최소 발주 수량을 설정한 값으로 수정합니다.")
    public ItemDetailResponse itemMinStockUpdate(
            @PathVariable Long popupId,
            @PathVariable Long itemId,
            @Valid @RequestBody ItemMinStockUpdateRequest request) {
        return itemService.updateItemMinStock(popupId, itemId, request);
    }

    @GetMapping("/dashboard/trending")
    @Operation(
            summary = "실시간 인기 상품 조회",
            description = "팝업 오픈부터 직전 타임까지의 관심도와 판매량을 기준으로 상위 3개의 인기 상품을 조회합니다.")
    public List<ItemTrendingResponse> TrendingItemFind(@PathVariable Long popupId) {
        return itemService.getTrendingItemsByManager(popupId);
    }
}
