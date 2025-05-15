package com.lgcns.domain.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Popularity {
    normal("일반 상품"),
    hot("인기 상품"),
    ;

    private final String description;
}
