package com.lgcns.domain.notification.domain;

import com.lgcns.global.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    private Long managerId;
    private Long popupId;
    private Long itemId;

    private String itemName;

    @Enumerated(EnumType.STRING)
    private Popularity popularity;

    private Integer minStock;

    @Builder
    private Notification(
            Long managerId,
            Long popupId,
            Long itemId,
            String itemName,
            Popularity popularity,
            Integer minStock) {
        this.managerId = managerId;
        this.popupId = popupId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.popularity = popularity;
        this.minStock = minStock;
    }

    public static Notification createNotification(
            Long managerId,
            Long popupId,
            Long itemId,
            String itemName,
            Popularity popularity,
            Integer minStock) {
        return Notification.builder()
                .managerId(managerId)
                .popupId(popupId)
                .itemId(itemId)
                .itemName(itemName)
                .popularity(popularity)
                .minStock(minStock)
                .build();
    }
}
