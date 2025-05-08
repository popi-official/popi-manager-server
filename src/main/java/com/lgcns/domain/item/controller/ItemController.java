package com.lgcns.domain.item.controller;

import com.lgcns.domain.item.dto.request.ItemCreateRequest;
import com.lgcns.domain.item.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "4. 상품 API", description = "상품 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/popups/{popupId}/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @Operation(summary = "단일 상품 등록", description = "단일 상품을 등록합니다.")
    public ResponseEntity<Void> itemCreate(
            @PathVariable Long popupId, @RequestBody @Valid ItemCreateRequest request) {

        itemService.createItem(popupId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/excel")
    @Operation(summary = "excel 상품 파일 등록", description = "excel 파일을 업로드하여 상품 리스트를 등록합니다.")
    public ResponseEntity<Void> itemCreateByExcel(
            @RequestPart(value = "itemFile") MultipartFile itemFile, @PathVariable Long popupId)
            throws IOException, InvalidFormatException {

        itemService.createItemByExcel(itemFile, popupId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
