package com.lgcns.global.aop;

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
            "execution(* org.springframework.context.ApplicationEventPublisher+.publishEvent(..))")
    public void publishEventMethod() {}

    @Pointcut("@annotation(org.springframework.context.event.EventListener)")
    public void eventListenerMethods() {}

    @Around("publishEventMethod()")
    public Object logEventPublishing(ProceedingJoinPoint joinPoint) throws Throwable {
        Object event = joinPoint.getArgs()[0];
        log.info("[Event] Publishing: {}", event);

        try {
            Object result = joinPoint.proceed();
            log.info("[Event] Published successfully: {}", event);
            return result;
        } catch (Exception e) {
            log.error("[Event] Failed to publish: {}", event, e);
            throw e;
        }
    }

    @Around("eventListenerMethods()")
    public Object logEventListener(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        Object event = joinPoint.getArgs()[0];
        log.info("[Event] Listen Start: {}, Event: {}", method, event);
        log.info("TraceId: {}", org.slf4j.MDC.get("traceId"));

        try {
            Object result = joinPoint.proceed();
            log.info("[Event] Listen End: {}", method);
            log.info("TraceId: {}", org.slf4j.MDC.get("traceId"));
            return result;
        } catch (Exception e) {
            log.error("[Event] Error in {}: {}", method, e.getMessage(), e);
            throw e;
        }
    }
}
