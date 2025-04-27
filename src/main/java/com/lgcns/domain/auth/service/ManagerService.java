package com.lgcns.domain.auth.service;

import com.lgcns.domain.auth.dto.Request.ManagerCreateRequest;

public interface ManagerService {
    void createManager(ManagerCreateRequest request);
}
