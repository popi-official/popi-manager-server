package com.lgcns.domain.survey.service;

import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.survey.dto.response.ChoiceStatsResponse;
import com.lgcns.domain.survey.dto.response.SurveyAnalysisResponse;
import com.lgcns.domain.survey.dto.response.SurveyResultResponse;
import com.lgcns.domain.survey.dto.response.SurveyStatsResponse;
import com.lgcns.domain.survey.repository.SurveyRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository surveyRepository;
    private final PopupRepository popupRepository;
    private final ManagerUtil managerUtil;

    @Override
    public SurveyAnalysisResponse getSurveyAnalysis(Long popupId) {

        Manager manager = managerUtil.getCurrentManager();
        Popup popup = findPopupById(popupId);
        validatePopupOwnership(manager, popup);

        List<SurveyResultResponse> results = surveyRepository.getSurveyResults(popupId);
        Long totalCount = surveyRepository.countMemberAnswerByPopup(popupId);

        Map<Integer, List<ChoiceStatsResponse>> groupedBySurvey =
                results.stream()
                        .collect(
                                Collectors.groupingBy(
                                        SurveyResultResponse::surveyNumber,
                                        Collectors.mapping(
                                                result ->
                                                        ChoiceStatsResponse.of(
                                                                result.choiceContent(),
                                                                result.memberAnswerCount(),
                                                                getMemberAnswerRatio(
                                                                        result.memberAnswerCount(),
                                                                        totalCount)),
                                                Collectors.toList())));

        List<SurveyStatsResponse> surveys =
                groupedBySurvey.entrySet().stream()
                        .map(entry -> SurveyStatsResponse.of(entry.getKey(), entry.getValue()))
                        .sorted(Comparator.comparing(SurveyStatsResponse::surveyNumber))
                        .toList();

        return SurveyAnalysisResponse.of(totalCount, surveys);
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

    private Integer getMemberAnswerRatio(Long memberAnswerCount, Long totalCount) {
        if (memberAnswerCount == 0 || totalCount == 0) {
            return 0;
        }
        double ratio = (memberAnswerCount * 100.0) / totalCount;
        return (int) Math.round(ratio);
    }
}
