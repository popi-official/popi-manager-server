package com.lgcns.domain.manager.service;

import com.lgcns.domain.manager.dto.request.ManagerCreateRequest;

public interface ManagerService {
    void createManager(ManagerCreateRequest request);

    void logoutManager();
}
