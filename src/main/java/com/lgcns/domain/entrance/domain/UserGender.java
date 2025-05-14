package com.lgcns.domain.entrance.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserGender {
    MALE("남성"),
    FEMALE("여성"),
    ;

    private final String gender;
}
