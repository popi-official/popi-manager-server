package com.lgcns.domain.survey.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ChoiceCreateRequest(
        @Schema(
                        description = "보기 항목",
                        example = "[\"보기1\", \"보기2\", \"보기3\", \"보기4\"]",
                        required = true)
                @Size(min = 2, max = 5, message = "보기 항목은 2개 이상 5개 이하여야 합니다.")
                List<String> optionList) {}
