package com.lgcns.domain.visitLog.service;

import com.lgcns.domain.visitLog.dto.request.EntranceCreateRequest;

public interface VisitLogService {
    void createEntrance(EntranceCreateRequest request);
}
