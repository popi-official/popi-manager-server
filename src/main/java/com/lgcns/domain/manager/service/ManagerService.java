package com.lgcns.domain.manager.service;

import com.lgcns.domain.manager.dto.ManagerCreateRequest;
import com.lgcns.domain.manager.dto.ManagerCreateResponse;

public interface ManagerService {
    ManagerCreateResponse createManager(ManagerCreateRequest request);
}
