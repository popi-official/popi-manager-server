package com.lgcns.domain.entrance.service;

import com.lgcns.domain.entrance.domain.Entrance;
import com.lgcns.domain.entrance.dto.request.EntranceCreateRequest;
import com.lgcns.domain.entrance.repository.EntranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EntranceServiceImpl implements EntranceService {

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
