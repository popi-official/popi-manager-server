package com.lgcns.domain.popup.internalApi;

import com.lgcns.domain.popup.dto.request.PopupIdsRequest;
import com.lgcns.domain.popup.dto.response.MemberReservationDetailResponse;
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
    @Operation(
            summary = "팝업 목록 조회",
            description =
                    "searchName을 포함하지 않으면 현재 예약가능한 모든 팝업을 무한 스크롤을 위한 페이징 처리한 뒤 반환합니다.</br>"
                            + "searchName을 포함하여 호출하면 팝업명에 searchName이 포함된 모든 팝업을 페이징 처리한 뒤 반환합니다.")
    public SliceResponse<PopupInfoResponse> popupFindAll(
            @Parameter(description = "검색할 팝업 이름 (비워두면 모든 팝업을 반환합니다.)", example = "블랙핑크")
                    @RequestParam(required = false)
                    String searchName,
            @Parameter(description = "이전 페이지의 마지막 ID (첫 요청 시 비워두세요.)", example = "2")
                    @RequestParam(required = false)
                    Long lastPopupId,
            @Parameter(description = "페이지 크기 (기본 8)", example = "8")
                    @RequestParam(defaultValue = "8")
                    int size) {
        return popupService.findPopupsByNameWithPagination(searchName, lastPopupId, size);
    }

    @GetMapping("/reservations/popups/{popupId}/survey")
    @Operation(summary = "팝업에 등록된 설문지 목록 조회", description = "팝업 ID를 통해 등록된 설문지 목록을 조회합니다.")
    public List<SurveyChoiceResponse> choiceListByPopupIdFind(@PathVariable Long popupId) {
        return popupService.findAllChoicesByPopupId(popupId);
    }

    @PostMapping("/reservations")
    @Operation(summary = "팝업 예약 목록 상세 조회", description = "사용자가 예약한 팝업들을 상세 조회합니다.")
    public List<MemberReservationDetailResponse> popupDetailsFind(
            @RequestBody PopupIdsRequest request) {
        return popupService.findPopupDetails(request);
    }
}
