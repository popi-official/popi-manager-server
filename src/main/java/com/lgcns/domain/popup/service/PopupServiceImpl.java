package com.lgcns.domain.popup.service;

import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.dto.request.PopupCreateRequest;
import com.lgcns.domain.popup.dto.request.PopupWithChoicesCreateRequest;
import com.lgcns.domain.popup.dto.response.PopupCreateResponse;
import com.lgcns.domain.popup.dto.response.PopupPreviewResponse;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.reservation.domain.Reservation;
import com.lgcns.domain.reservation.repository.ReservationRepository;
import com.lgcns.domain.survey.domain.Choice;
import com.lgcns.domain.survey.domain.Survey;
import com.lgcns.domain.survey.dto.request.ChoiceCreateRequest;
import com.lgcns.domain.survey.repository.ChoiceRepository;
import com.lgcns.domain.survey.repository.SurveyRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private final ReservationRepository reservationRepository;
    private final ManagerUtil managerUtil;

    private final int MAX_SURVEY = 4;

    @Override
    public PopupCreateResponse createPopup(
            PopupWithChoicesCreateRequest popupWithChoicesCreateRequest) {
        Manager manager = managerUtil.getCurrentManager();
        Popup popup =
                createPopupFromRequest(manager, popupWithChoicesCreateRequest.popupCreateRequest());
        popupRepository.save(popup);

        createSurveyFromRequest(popup, popupWithChoicesCreateRequest.choiceCreateRequestList());
        createReservation(popup);

        return PopupCreateResponse.of(popup.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PopupPreviewResponse> findAllPopups() {
        Long managerId = managerUtil.getCurrentManagerId();
        return popupRepository.findAllPopupsByManagerId(managerId);
    }

    @Override
    public void deletePopup(Long popupId) {
        final Manager currentManager = managerUtil.getCurrentManager();
        final Popup popup = findPopupById(popupId);

        validatePopupOwnership(currentManager, popup);

        popupRepository.delete(popup);
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
        for (int i = 1; i <= MAX_SURVEY; i++) {
            Survey survey = surveyRepository.save(Survey.createSurvey(popup, i));
            createChoiceFromRequest(survey, choiceCreateRequestList.get(i - 1));
        }
    }

    private void createChoiceFromRequest(Survey survey, ChoiceCreateRequest choiceCreateRequest) {
        List<Choice> choiceList = new ArrayList<>();
        for (int i = 1; i <= choiceCreateRequest.optionList().size(); i++) {
            String option = choiceCreateRequest.optionList().get(i - 1);
            choiceList.add(Choice.createChoice(survey, option, i));
        }
        choiceRepository.saveAll(choiceList);
    }

    private void validatePopupOwnership(Manager manager, Popup popup) {
        if (!popup.getManager().equals(manager)) {
            throw new CustomException(PopupErrorCode.POPUP_UNAUTHORIZED);
        }
    }

    private Popup findPopupById(Long popupId) {
        return popupRepository
                .findById(popupId)
                .orElseThrow(() -> new CustomException(PopupErrorCode.POPUP_NOT_FOUND));
    }

    private void createReservation(Popup popup) {
        List<Reservation> reservationList = new ArrayList<>();

        for (LocalDate reservationDate = popup.getPopupStartDate();
                reservationDate.isBefore(popup.getPopupEndDate().plusDays(1));
                reservationDate = reservationDate.plusDays(1)) {

            for (LocalTime reservationTime = popup.getRunOpenTime();
                    reservationTime.isBefore(popup.getRunCloseTime().plusHours(1));
                    reservationTime = reservationTime.plusHours(1)) {

                Reservation reservation =
                        Reservation.createReservation(
                                popup,
                                reservationDate,
                                reservationTime,
                                popup.getTimeCapacity(),
                                popup.getReservationOpenDateTime(),
                                LocalDateTime.of(reservationDate, reservationTime)
                                                .isBefore(popup.getReservationCloseDateTime())
                                        ? popup.getReservationCloseDateTime()
                                        : LocalDateTime.of(reservationDate, reservationTime));
                reservationList.add(reservation);
            }
        }

        reservationRepository.bulkInsertReservations(reservationList);
    }
}
