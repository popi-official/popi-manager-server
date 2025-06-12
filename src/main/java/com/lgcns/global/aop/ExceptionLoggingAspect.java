package com.lgcns.global.aop;

import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.error.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionLoggingAspect {

    @AfterThrowing(pointcut = "execution(* com.lgcns.domain..service..*(..))", throwing = "e")
    public void logException(JoinPoint joinPoint, Exception e) {
        String method = joinPoint.getSignature().toShortString();

        if (e instanceof CustomException customException) {
            ErrorCode errorCode = customException.getErrorCode();
            log.info(
                    "[CustomException] Method: {}, Code: {}, Message: {}",
                    method,
                    errorCode,
                    customException.getMessage());
        } else {
            log.error(
                    "[UnhandledException] Method: {}, Exception: {}, Message: {}",
                    method,
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    e);
        }
    }
}
