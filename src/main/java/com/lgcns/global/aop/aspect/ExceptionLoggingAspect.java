package com.lgcns.global.aop.aspect;

import com.lgcns.global.aop.util.LoggingUtil;
import com.lgcns.global.error.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionLoggingAspect {

    @Pointcut("execution(public * com.lgcns.domain..service..*(..))")
    public void serviceLayerMethods() {}

    @AfterThrowing(pointcut = "serviceLayerMethods()", throwing = "e")
    public void serviceExceptionLogging(JoinPoint joinPoint, Exception e) {
        String method = joinPoint.getSignature().toShortString();
        String traceId = LoggingUtil.getTraceId();
        Long startTime = System.currentTimeMillis();

        if (e instanceof CustomException customException) {
            log.info(
                    "[CustomException] TraceId: {}, Method: {}, Code: {}, Message: {}",
                    traceId,
                    method,
                    customException.getErrorCode(),
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
