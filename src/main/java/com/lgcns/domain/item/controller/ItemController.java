package com.lgcns.domain.item.controller;

import com.lgcns.domain.item.dto.request.ItemCreateRequest;
import com.lgcns.domain.item.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "3. 상품 API", description = "상품 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @Operation(summary = "단일 상품 등록", description = "단일 상품 정보를 등록합니다.")
    public ResponseEntity<Void> itemCreated(@RequestBody @Valid ItemCreateRequest request) {

        itemService.createItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
