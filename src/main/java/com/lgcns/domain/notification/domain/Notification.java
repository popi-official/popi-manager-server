package com.lgcns.domain.notification.domain;

import com.lgcns.global.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
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

    private Long userId;
    private Long popupId;
    private Long itemId;

    private String itemName;
    private Popularity popularity;
    private Integer minStock;
}
