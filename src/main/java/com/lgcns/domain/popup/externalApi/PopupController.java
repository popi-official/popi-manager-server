package com.lgcns.domain.popup.externalApi;

import com.lgcns.domain.popup.dto.request.PopupWithChoicesCreateRequest;
import com.lgcns.domain.popup.dto.response.PopupCreateResponse;
import com.lgcns.domain.popup.dto.response.PopupPreviewResponse;
import com.lgcns.domain.popup.service.PopupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/popups")
@RequiredArgsConstructor
@Tag(name = "3-1. 팝업 API", description = "팝업 관련 API 입니다.")
public class PopupController {

    private final PopupService popupService;

    @PostMapping
    @Operation(summary = "팝업 등록", description = "팝업과 설문지, 보기 문항을 모두 작성한 후 등록합니다.")
    public ResponseEntity<PopupCreateResponse> popupCreate(
            @RequestBody @Valid PopupWithChoicesCreateRequest popupWithChoicesCreateRequest) {
        PopupCreateResponse response = popupService.createPopup(popupWithChoicesCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "팝업 목록 조회", description = "로그인한 운영자의 모든 팝업스토어 목록을 조회합니다.")
    public List<PopupPreviewResponse> popupFindAll() {
        return popupService.findAllPopups();
    }

    @DeleteMapping("/{popupId}")
    @Operation(summary = "팝업 삭제", description = "로그인한 운영자 소유의 팝업 하나를 삭제합니다.")
    public ResponseEntity<Void> popupDelete(@PathVariable Long popupId) {
        popupService.deletePopup(popupId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
