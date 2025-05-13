package com.lgcns.domain.visitLog.service;

import com.lgcns.domain.visitLog.domain.Entrance;
import com.lgcns.domain.visitLog.dto.request.EntranceCreateRequest;
import com.lgcns.domain.visitLog.repository.EntranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitLogServiceImpl implements VisitLogService {

    private final EntranceRepository entranceRepository;

    @Override
    public void createEntrance(EntranceCreateRequest request) {
        Entrance entrance =
                Entrance.createPopupEnter(
                        request.popupId(),
                        request.gender(),
                        request.ageGroup(),
                        request.date(),
                        request.time());

        entranceRepository.save(entrance);
    }
}
