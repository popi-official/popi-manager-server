package com.lgcns.domain.survey.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/popups/{popupId}/dashboard/surveys")
@RequiredArgsConstructor
@Tag(name = "8. 설문지 API")
public class SurveyController {}
