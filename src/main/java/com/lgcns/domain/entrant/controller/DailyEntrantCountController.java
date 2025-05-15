package com.lgcns.domain.entrant.controller;

import com.lgcns.domain.entrant.dto.response.DailyEntrantCountResponse;
import com.lgcns.domain.entrant.service.DailyEntrantCountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/popups/{popupId}/dashboard/entrants")
@RequiredArgsConstructor
@Tag(name = "10. 일일 입장자 수 조회 API", description = "일일 입장자 수 조회 API입니다.")
public class DailyEntrantCountController {

    private final DailyEntrantCountService dailyEntrantCountService;

    @GetMapping
    @Operation(summary = "일일 입장자 수 조회", description = "일일 입장자 수를 조회합니다.")
    public DailyEntrantCountResponse dailyEntrantCountFind(@PathVariable Long popupId) {
        return dailyEntrantCountService.findDailyEntrantCount(popupId);
    }
}
