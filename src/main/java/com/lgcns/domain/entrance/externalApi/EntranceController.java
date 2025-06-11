package com.lgcns.domain.entrance.externalApi;

import com.lgcns.domain.entrance.dto.response.DailyEntrantCountResponse;
import com.lgcns.domain.entrance.service.EntranceService;
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
@Tag(name = "06. 입장 API", description = "입장 관련 API 입니다.")
public class EntranceController {

    private final EntranceService entranceService;

    @GetMapping
    @Operation(summary = "일일 입장자 수 조회", description = "일일 입장자 수를 조회합니다.")
    public DailyEntrantCountResponse dailyEntrantCountFind(@PathVariable Long popupId) {
        return entranceService.findDailyEntrantCount(popupId);
    }
}
