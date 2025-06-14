package com.lgcns.global.aop.aspect;

import com.lgcns.global.aop.util.LoggingUtil;
import com.lgcns.global.error.exception.CustomException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
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
public class KafkaLoggingAspect {

    @Pointcut("@annotation(org.springframework.kafka.annotation.KafkaListener)")
    public void kafkaListenerMethods() {}

    @Around("kafkaListenerMethods()")
    public Object logKafkaListener(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = signature.toShortString();
        Object[] args = joinPoint.getArgs();
        String traceId = LoggingUtil.getTraceId();
        String memberId = LoggingUtil.getMemberId();
        Map<String, Object> params = LoggingUtil.extractParams(method, args);

        try {
            Object result = joinPoint.proceed();
            log.info(
                    "[KAFKA] TraceId: {}, MemberId: {}, Method: {}, Params: {}",
                    LoggingUtil.getTraceId(),
                    LoggingUtil.getMemberId(),
                    methodName,
                    Arrays.toString(args));
            return result;
        } catch (Exception e) {
            log.error(
                    "[KAFKA-ERROR] TraceId: {}, MemberId: {}, Method: {}, Params: {}, Exception: {}, Message: {}",
                    traceId,
                    memberId,
                    methodName,
                    params,
                    e.getClass().getSimpleName(),
                    e.getMessage());
            throw e;
        }
    }

    @Pointcut("execution(* com.lgcns..producer..*Producer.sendMessage(..))")
    public void allKafkaProducer() {}

    @Around("allKafkaProducer()")
    public Object logKafkaProducer(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = LoggingUtil.getMethodSignature(method);
        Map<String, Object> params = LoggingUtil.extractParams(method, joinPoint.getArgs());

        String traceId = LoggingUtil.getTraceId();
        String memberId = LoggingUtil.getMemberId();

        try {
            return joinPoint.proceed();
        } catch (CustomException ce) {
            log.info(
                    "[KAFKA-CUSTOM] TraceId: {}, MemberId: {}, Method: {}, Params: {}, Code: {}, Message: {}, Duration: {}",
                    traceId,
                    memberId,
                    methodName,
                    params,
                    ce.getErrorCode(),
                    ce.getMessage(),
                    LoggingUtil.calculateDuration(System.currentTimeMillis()));
            throw ce;
        } catch (Exception e) {
            log.error(
                    "[KAFKA-ERROR] TraceId: {}, MemberId: {}, Method: {}, Params: {}, Exception: {}, Message: {}",
                    traceId,
                    memberId,
                    methodName,
                    params,
                    e.getClass().getSimpleName(),
                    e.getMessage());
            throw e;
        }
    }
}
