package com.lgcns.global.aop.aspect;

import static com.lgcns.global.aop.util.LoggingUtil.clearMDC;

import com.lgcns.global.aop.util.LoggingUtil;
import com.lgcns.global.error.exception.CustomException;
import java.lang.reflect.Method;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class SchedulerLoggingAspect {

    @Pointcut("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void allScheduledJobs() {}

    @Around("allScheduledJobs()")
    public Object logScheduler(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = LoggingUtil.getMethodSignature(method);

        String traceId = UUID.randomUUID().toString();
        LoggingUtil.setTraceId(traceId);

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;

            log.info(
                    "[SCHEDULER] TraceId: {}, Method: {}, Duration: {}ms",
                    traceId,
                    methodName,
                    duration);
            return result;

        } catch (CustomException ce) {
            log.info(
                    "[SCHEDULER-CUSTOM] TraceId: {}, Method: {}, Code: {}, Message: {}, Duration: {}ms",
                    traceId,
                    methodName,
                    ce.getErrorCode(),
                    ce.getMessage(),
                    System.currentTimeMillis() - start);
            throw ce;

        } catch (Exception e) {
            log.error(
                    "[SCHEDULER-ERROR] TraceId: {}, Method: {}, Exception: {}, Message: {}, Duration: {}ms",
                    traceId,
                    methodName,
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    System.currentTimeMillis() - start);
            throw e;
        } finally {
            clearMDC();
        }
    }
}
