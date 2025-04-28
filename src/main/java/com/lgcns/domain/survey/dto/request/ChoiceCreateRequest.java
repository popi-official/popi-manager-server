package com.lgcns.domain.survey.dto.request;

import jakarta.validation.constraints.Size;
import java.util.List;

public record ChoiceCreateRequest(
        @Size(min = 2, max = 5, message = "보기 항목은 2개 이상 5개 이하여야 합니다.") List<String> optionList) {}
