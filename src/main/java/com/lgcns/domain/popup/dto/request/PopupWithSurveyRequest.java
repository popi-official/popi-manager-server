package com.lgcns.domain.popup.dto.request;

import com.lgcns.domain.survey.dto.request.ChoiceCreateRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PopupWithSurveyRequest(
        @NotNull PopupCreateRequest popupCreateRequest,
        @NotNull @Size(min = 4, max = 4, message = "총 4개의 항목이 필요합니다.")
                List<ChoiceCreateRequest> choiceCreateRequestList) {}
