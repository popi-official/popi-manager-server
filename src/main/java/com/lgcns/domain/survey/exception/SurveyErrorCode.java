package com.lgcns.domain.survey.error;

import com.lgcns.global.error.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SurveyErrorCode implements ErrorCode {
    SURVEY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 설문이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
