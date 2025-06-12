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
public class SchedulerLoggingAspect {

    @Pointcut("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void scheduledMethods() {}

    @Around("scheduledMethods()")
    public Object logScheduledExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("[SCHEDULER] Start: {}", methodName);

        try {
            Object result = joinPoint.proceed();
            log.info("[SCHEDULER] End: {}", methodName);
            return result;
        } catch (Throwable e) {
            log.error("[SCHEDULER] Exception in: {}", methodName, e);
            throw e;
        }
    }
}
