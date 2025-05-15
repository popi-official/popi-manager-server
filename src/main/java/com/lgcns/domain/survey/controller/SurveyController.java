package com.lgcns.domain.survey.controller;

import com.lgcns.domain.survey.dto.response.SurveyAnalysisResponse;
import com.lgcns.domain.survey.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/popups/{popupId}/dashboard/surveys")
@RequiredArgsConstructor
@Tag(name = "8. 설문지 분석 API", description = "설문지 분석 관련 API 입니다.")
public class SurveyController {

    private final SurveyService surveyService;

    @GetMapping
    @Operation(summary = "설문지 분석 조회", description = "전체 응답자 수와 함께 설문지 문항 별 분석 내용을 조회합니다.")
    public SurveyAnalysisResponse surveyAnalysisGet(@PathVariable(name = "popupId") Long popupId) {
        return surveyService.getSurveyAnalysis(popupId);
    }
}
