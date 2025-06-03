package com.lgcns.domain.survey.repository;

import com.lgcns.domain.survey.domain.Choice;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    List<Choice> findBySurveyId(Long surveyId);
}
