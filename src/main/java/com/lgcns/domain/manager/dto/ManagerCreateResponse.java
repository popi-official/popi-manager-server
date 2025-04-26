package com.lgcns.domain.manager.dto;

public record ManagerCreateResponse(Long id, String username) {
    public static ManagerCreateResponse from(Long id, String username) {
        return new ManagerCreateResponse(id, username);
    }
}
