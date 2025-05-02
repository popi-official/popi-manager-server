package com.lgcns.domain.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ManagerRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    ;

    private final String roleName;
}
