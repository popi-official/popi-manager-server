package com.lgcns.domain.popupAccess.service;

import com.lgcns.domain.popupAccess.domain.PopupEnter;
import com.lgcns.domain.popupAccess.dto.request.PopupEnterCreateRequest;
import com.lgcns.domain.popupAccess.repository.PopupEnterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PopupAccessServiceImpl implements PopupAccessService {

    private final PopupEnterRepository popupEnterRepository;

    @Override
    public void createPopupEnter(Long popupId, PopupEnterCreateRequest request) {
        PopupEnter popupEnter =
                PopupEnter.createPopupEnter(
                        popupId,
                        request.gender(),
                        request.ageGroup(),
                        request.date(),
                        request.time());

        popupEnterRepository.save(popupEnter);
    }
}
