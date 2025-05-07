package com.lgcns.domain.popup.dto.request;

import com.lgcns.domain.survey.dto.request.ChoiceCreateRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PopupWithChoicesCreateRequest(
        @Schema(description = "팝업스토어 생성 요청 DTO", required = true)
                @NotNull(message = "팝업스토어 생성 DTO는 필수입니다.")
                @Valid
                PopupCreateRequest popupCreateRequest,
        @Schema(description = "설문조사 생성 요청 DTO", required = true)
                @NotNull(message = "설문조사 보기 DTO는 필수입니다.")
                @Size(min = 4, max = 4, message = "총 4개의 항목이 필요합니다.")
                @Valid
                List<ChoiceCreateRequest> choiceCreateRequestList) {}
