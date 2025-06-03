package com.lgcns.domain.paymentStats.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AveragePeriod {
    TOTAL("TOTAL"),
    TODAY("TODAY"),
    ;

    private final String value;
}
