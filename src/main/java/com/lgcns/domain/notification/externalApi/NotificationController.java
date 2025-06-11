package com.lgcns.domain.notification.externalApi;

import com.lgcns.domain.notification.dto.response.NotificationResponse;
import com.lgcns.domain.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/popups/{popupId}/notifications")
@RequiredArgsConstructor
@Tag(name = "07. 알림 API", description = "알림 관련 API입니다.")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/stock")
    @Operation(summary = "알림 목록 조회", description = "알림 목록을 조회합니다.")
    public List<NotificationResponse> getNotificationList(@PathVariable Long popupId) {
        return notificationService.findNotificationList(popupId);
    }
}
