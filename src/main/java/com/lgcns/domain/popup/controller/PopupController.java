package com.lgcns.domain.popup.controller;

import com.lgcns.domain.popup.dto.request.PopupWithChoicesCreateRequest;
import com.lgcns.domain.popup.dto.response.PopupCreateResponse;
import com.lgcns.domain.popup.service.PopupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/popups")
@RequiredArgsConstructor
@Tag(name = "2. 팝업 API", description = "팝업 관련 API 입니다.")
public class PopupController {

    private final PopupService popupService;

    @PostMapping
    @Operation(summary = "팝업 등록", description = "팝업과 설문지, 보기 문항을 모두 작성한 후 등록합니다.")
    public ResponseEntity<PopupCreateResponse> popupCreate(
            @RequestBody @Valid PopupWithChoicesCreateRequest popupWithChoicesCreateRequest) {
        PopupCreateResponse response = popupService.createPopup(popupWithChoicesCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
