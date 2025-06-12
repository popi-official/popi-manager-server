package com.lgcns.global.aop;

import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.error.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionLoggingAspect {

    @AfterThrowing(pointcut = "execution(* com.lgcns.domain..service..*(..))", throwing = "e")
    public void serviceExceptionLogging(JoinPoint joinPoint, Exception e) {
        String method = joinPoint.getSignature().toShortString();
        String traceId = MDC.get("traceId");

        if (e instanceof CustomException customException) {
            ErrorCode errorCode = customException.getErrorCode();
            log.info(
                    "[CustomException] TraceId: {}, Method: {}, Code: {}, Message: {}",
                    traceId,
                    method,
                    errorCode,
                    customException.getMessage());
        } else {
            log.error(
                    "[UnhandledException] TraceId: {}, Method: {}, Exception: {}, Message: {}",
                    traceId,
                    method,
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    e);
        }
    }
}
