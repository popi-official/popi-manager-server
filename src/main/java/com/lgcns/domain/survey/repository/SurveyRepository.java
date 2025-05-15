package com.lgcns.domain.survey.repository;

import com.lgcns.domain.survey.domain.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long>, SurveyRepositoryCustom {}
