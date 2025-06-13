package com.lgcns.global.aop;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class KafkaLoggingAspect {

    @Pointcut("@annotation(org.springframework.kafka.annotation.KafkaListener)")
    public void kafkaListenerMethods() {}

    @Around("kafkaListenerMethods()")
    public Object logKafkaListener(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.info(
                "[KAFKA] Consuming message in {} with args: {}", methodName, Arrays.toString(args));

        try {
            Object result = joinPoint.proceed();
            log.info("[KAFKA] Finished processing in {}", methodName);
            return result;
        } catch (Exception e) {
            log.error("[KAFKA] Error while processing in {}: {}", methodName, e.getMessage(), e);
            throw e;
        }
    }
}
