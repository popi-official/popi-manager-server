package com.lgcns.global.aop.aspect;

import static com.lgcns.global.aop.util.LoggingUtil.*;

import java.lang.reflect.Method;
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
public class ServiceLoggingAspect {

    @Pointcut("execution(public * com.lgcns..service..*.*(..))")
    public void allService() {}

    @Around("allService()")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = getMethodSignature(method);
        String traceId = getTraceId();
        String memberId = getMemberId();

        long start = System.currentTimeMillis();

        log.info("[SERVICE] TraceId: {}, MemberId: {}, Method: {}", traceId, memberId, methodName);

        try {
            return joinPoint.proceed();
        } finally {

            log.info(
                    "[SERVICE] TraceId: {}, MemberId: {}, Method: {}, Duration: {}ms",
                    traceId,
                    memberId,
                    methodName,
                    calculateDuration(start));
        }
    }
}
