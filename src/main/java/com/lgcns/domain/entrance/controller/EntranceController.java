package com.lgcns.domain.entrance.controller;

import com.lgcns.domain.entrance.dto.request.EntranceCreateRequest;
import com.lgcns.domain.entrance.service.EntranceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "6. 방문자 입장 기록 API", description = "QR 인식 후 방문자의 입장 정보를 기록하는 API입니다.")
public class EntranceController {

    private final EntranceService entranceService;

    @PostMapping("/entrances")
    @Operation(summary = "방문자 입장 기록", description = "QR 인식이 끝난 방문자 정보를 받아 방문자에 대한 정보를 기록합니다.")
    public ResponseEntity<Void> entranceCreate(@Valid @RequestBody EntranceCreateRequest request) {
        entranceService.createEntrance(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
