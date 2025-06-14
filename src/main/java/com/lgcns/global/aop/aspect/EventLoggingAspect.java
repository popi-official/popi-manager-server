package com.lgcns.global.aop.aspect;

import static com.lgcns.global.aop.util.LoggingUtil.calculateDuration;

import com.lgcns.global.aop.util.LoggingUtil;
import com.lgcns.global.error.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class EventLoggingAspect {

    @Pointcut(
            "execution(public * org.springframework.context.ApplicationEventPublisher+.publishEvent(..))")
    public void publishEventMethod() {}

    @Pointcut(
            "@annotation(org.springframework.context.event.EventListener) || "
                    + "@annotation(org.springframework.transaction.event.TransactionalEventListener)")
    public void eventListenerMethods() {}

    @Around("publishEventMethod()")
    public Object logEventPublishing(ProceedingJoinPoint joinPoint) throws Throwable {
        Object event = joinPoint.getArgs()[0];
        long start = System.currentTimeMillis();
        String traceId = LoggingUtil.getTraceId();

        try {
            Object result = joinPoint.proceed();
            log.info(
                    "[EVENT-PUBLISH] TraceId: {}, Event: {}, Duration: {}ms",
                    traceId,
                    event,
                    calculateDuration(start));
            return result;
        } catch (Exception e) {
            log.error(
                    "[EVENT-PUBLISH-ERROR] TraceId: {}, Event: {}, Exception: {}, Duration: {}ms",
                    traceId,
                    event,
                    e.getMessage(),
                    calculateDuration(start));
            throw e;
        }
    }

    @Around("eventListenerMethods()")
    public Object logEventListener(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        Object event = joinPoint.getArgs()[0];
        String traceId = LoggingUtil.getTraceId();
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            log.info(
                    "[EVENT-LISTENER] TraceId: {}, Method: {}, Event: {}, Duration: {}ms",
                    traceId,
                    method,
                    event,
                    calculateDuration(start));
            return result;
        } catch (CustomException ce) {
            log.info(
                    "[EVENT-LISTENER-CUSTOM] TraceId: {}, Method: {}, Event: {}, Code: {}, Message: {}, Duration: {}ms",
                    traceId,
                    method,
                    event,
                    ce.getErrorCode(),
                    ce.getMessage(),
                    calculateDuration(start));
            throw ce;
        } catch (Exception e) {
            log.error(
                    "[EVENT-LISTENER-ERROR] TraceId: {}, Method: {}, Event: {}, Exception: {}, Message: {}, Duration: {}ms",
                    traceId,
                    method,
                    event,
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    calculateDuration(start));
            throw e;
        }
    }
}
