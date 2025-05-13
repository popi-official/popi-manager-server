package com.lgcns.domain.visitLog.controller;

import com.lgcns.domain.visitLog.dto.request.EntranceCreateRequest;
import com.lgcns.domain.visitLog.service.VisitLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "6. 방문자 입·퇴장 기록 API", description = "QR 인식을 통해 방문자의 입장 및 퇴장 정보를 기록하는 API입니다.")
public class VisitLogController {

    private final VisitLogService visitLogService;

    @PostMapping("/entrances")
    @Operation(summary = "방문자 입장 기록", description = "QR 인식이 끝난 방문자 정보를 받아 방문자에 대한 정보를 기록합니다.")
    public ResponseEntity<Void> entranceCreate(@Valid @RequestBody EntranceCreateRequest request) {
        visitLogService.createEntrance(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
