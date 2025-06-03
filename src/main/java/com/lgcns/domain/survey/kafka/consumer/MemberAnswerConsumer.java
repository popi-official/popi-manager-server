package com.lgcns.domain.survey.kafka.consumer;

import com.lgcns.domain.survey.domain.MemberAnswer;
import com.lgcns.domain.survey.domain.Survey;
import com.lgcns.domain.survey.exception.SurveyErrorCode;
import com.lgcns.domain.survey.kafka.dto.SurveyChoiceDto;
import com.lgcns.domain.survey.kafka.message.MemberAnswerMessage;
import com.lgcns.domain.survey.repository.MemberAnswerRepository;
import com.lgcns.domain.survey.repository.SurveyRepository;
import com.lgcns.global.error.exception.CustomException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberAnswerConsumer {

    private static final String TOPIC = "member-answer-topic";

    private final SurveyRepository surveyRepository;
    private final MemberAnswerRepository memberAnswerRepository;

    @KafkaListener(
            topics = TOPIC,
            groupId = "member-answer",
            containerFactory = "memberAnswerKafkaListenerContainerFactory")
    public void createMemberAnswer(MemberAnswerMessage message) {

        List<Long> surveyIds =
                message.surveyChoices().stream().map(SurveyChoiceDto::surveyId).toList();

        List<Survey> surveys = surveyRepository.findAllById(surveyIds);

        Map<Long, Survey> surveyMap =
                surveys.stream().collect(Collectors.toMap(Survey::getId, survey -> survey));

        List<MemberAnswer> memberAnswers =
                message.surveyChoices().stream()
                        .map(
                                surveyChoice -> {
                                    Survey survey = surveyMap.get(surveyChoice.surveyId());

                                    if (survey == null) {
                                        throw new CustomException(SurveyErrorCode.SURVEY_NOT_FOUND);
                                    }

                                    return MemberAnswer.createMemberAnswer(
                                            survey, surveyChoice.choiceId(), message.memberId());
                                })
                        .toList();

        memberAnswerRepository.saveAll(memberAnswers);
    }
}
