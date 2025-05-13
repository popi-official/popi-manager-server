package com.lgcns.domain.popupAccess.controller;

import com.lgcns.domain.popupAccess.dto.request.PopupEnterCreateRequest;
import com.lgcns.domain.popupAccess.service.PopupAccessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/popups/{popupId}")
@RequiredArgsConstructor
@Tag(name = "6. 방문자 출입 기록 API", description = "QR 인식을 통해 입퇴장 기록을 수집하는 API입니다.")
public class PopupAccessController {

    private final PopupAccessService popupAccessService;

    @PostMapping("/dashboard/enter")
    @Operation(summary = "방문자 입장 기록", description = "QR 인식이 끝난 방문자 정보를 받아 방문자에 대한 정보를 기록합니다.")
    public ResponseEntity<Void> popupEnterCreate(
            @PathVariable Long popupId, @Valid @RequestBody PopupEnterCreateRequest request) {
        popupAccessService.createPopupEnter(popupId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
