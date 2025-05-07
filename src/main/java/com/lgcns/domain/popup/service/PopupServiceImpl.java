package com.lgcns.domain.popup.service;

import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.dto.request.PopupCreateRequest;
import com.lgcns.domain.popup.dto.request.PopupWithChoicesCreateRequest;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.survey.domain.Choice;
import com.lgcns.domain.survey.domain.Survey;
import com.lgcns.domain.survey.dto.request.ChoiceCreateRequest;
import com.lgcns.domain.survey.repository.ChoiceRepository;
import com.lgcns.domain.survey.repository.SurveyRepository;
import com.lgcns.global.util.ManagerUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PopupServiceImpl implements PopupService {
    private final PopupRepository popupRepository;
    private final SurveyRepository surveyRepository;
    private final ChoiceRepository choiceRepository;
    private final ManagerUtil managerUtil;

    @Override
    public void createPopup(PopupWithChoicesCreateRequest popupWithChoicesCreateRequest) {
        Manager manager = managerUtil.getCurrentManager();
        Popup popup =
                createPopupFromRequest(manager, popupWithChoicesCreateRequest.popupCreateRequest());
        popupRepository.save(popup);

        createSurveyFromRequest(popup, popupWithChoicesCreateRequest.choiceCreateRequestList());
    }

    private Popup createPopupFromRequest(Manager manager, PopupCreateRequest popupCreateRequest) {

        return Popup.createPopup(
                manager,
                popupCreateRequest.name(),
                popupCreateRequest.imageUrl(),
                popupCreateRequest.popupStartDate(),
                popupCreateRequest.popupEndDate(),
                popupCreateRequest.reservationOpenDateTime(),
                popupCreateRequest.reservationCloseDateTime(),
                popupCreateRequest.runOpenTime(),
                popupCreateRequest.runCloseTime(),
                popupCreateRequest.totalCapacity(),
                popupCreateRequest.timeCapacity(),
                popupCreateRequest.roadAddress(),
                popupCreateRequest.detailAddress(),
                popupCreateRequest.latitude(),
                popupCreateRequest.longitude());
    }

    private void createSurveyFromRequest(
            Popup popup, List<ChoiceCreateRequest> choiceCreateRequestList) {
        for (int i = 0; i < 4; i++) {
            Survey survey = surveyRepository.save(Survey.createSurvey(popup, i));
            createChoiceFromRequest(survey, choiceCreateRequestList.get(i));
        }
    }

    private void createChoiceFromRequest(Survey survey, ChoiceCreateRequest choiceCreateRequest) {
        for (String option : choiceCreateRequest.optionList()) {
            Choice choice = Choice.createChoice(survey, option);
            choiceRepository.save(choice);
        }
    }
}
