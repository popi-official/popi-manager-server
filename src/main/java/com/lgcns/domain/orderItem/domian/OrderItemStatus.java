package com.lgcns.domain.orderItem.domian;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderItemStatus {
    PENDING("PENDING"),
    COMPLETED("COMPLETED"),
    CANCELED("CANCELED");

    private final String description;
}
