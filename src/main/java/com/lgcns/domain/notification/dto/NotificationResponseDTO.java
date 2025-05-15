package com.lgcns.domain.notification.dto;

import com.lgcns.domain.notification.domain.Popularity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record NotificationResponseDTO(
        @Schema(description = "Notification ID", example = "1") Long notificationId,
        @Schema(description = "인기상품 여부", example = "NORMAL") Popularity popularity,
        @Schema(description = "상품 이름", example = "응원봉 V1") String itemName,
        @Schema(description = "재고 수량", example = "10") Integer minStock,
        @Schema(description = "알림 생성 시간", example = "2023-10-01T12:00:00")
                LocalDateTime notifiedAt) {
    public static NotificationResponseDTO of(
            Long notificationId,
            Popularity popularity,
            String name,
            Integer minStock,
            LocalDateTime notifiedAt) {
        return new NotificationResponseDTO(notificationId, popularity, name, minStock, notifiedAt);
    }
}
