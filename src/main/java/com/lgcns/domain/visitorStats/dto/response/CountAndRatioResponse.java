package com.lgcns.domain.visitorStats.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CountAndRatioResponse(
        @Schema(description = "그룹 이름", example = "age") String name,
        @Schema(description = "인원 수", example = "50") Integer count,
        @Schema(description = "인원 비율", example = "13") Integer ratio) {
    public static CountAndRatioResponse of(String name, Integer count, Integer ratio) {
        return new CountAndRatioResponse(name, count, ratio);
    }
}
