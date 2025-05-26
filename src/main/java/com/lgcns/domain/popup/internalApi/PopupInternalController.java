package com.lgcns.domain.popup.internalApi;

import com.lgcns.domain.popup.dto.response.PopupInfoResponse;
import com.lgcns.domain.popup.dto.response.SurveyChoiceResponse;
import com.lgcns.domain.popup.service.PopupService;
import com.lgcns.global.common.response.SliceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
@Tag(name = "3-2. 팝업 Internal API", description = "팝업 관련 Internal API 입니다.")
public class PopupInternalController {

    private final PopupService popupService;

    @GetMapping("/popups")
    @Operation(summary = "팝업 목록 조회", description = "현재 운영중인 모든 팝업 스토어 목록을 조회합니다.")
    public SliceResponse<PopupInfoResponse> popupFindAll(
            @Parameter(description = "이전 페이지의 마지막 ID (첫 요청 시 비워두세요.)", example = "2")
                    @RequestParam(required = false)
                    Long lastPopupId,
            @Parameter(description = "페이지 크기 (기본 8)", example = "8")
                    @RequestParam(defaultValue = "8")
                    int size) {
        return popupService.findAllActivePopups(lastPopupId, size);
    }

    @GetMapping("/reservations/popups/{popupId}/survey")
    @Operation(summary = "팝업에 등록된 설문지 목록 조회", description = "팝업 ID를 통해 등록된 설문지 목록을 조회합니다.")
    public List<SurveyChoiceResponse> choiceListByPopupIdFind(@PathVariable Long popupId) {
        return popupService.findAllChoicesByPopupId(popupId);
    }
}
