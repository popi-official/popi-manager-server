package com.lgcns.global.error.feign;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgcns.global.aop.util.LoggingUtil;
import com.lgcns.global.error.exception.CustomException;
import feign.RequestInterceptor;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.InputStream;
import org.springframework.context.annotation.Bean;

public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        try (InputStream body = response.body().asInputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode errorBody = objectMapper.readTree(body);

            String errorClassName =
                    errorBody.path("data").path("errorClassName").asText("FEIGN_ERROR");
            String message = errorBody.path("data").path("message").asText("Feign 예외 디코딩 실패");

            return new CustomException(
                    new FeignErrorCode(errorClassName, message, response.status()));
        } catch (Exception e) {
            return defaultDecoder.decode(methodKey, response);
        }
    }

    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return template -> {
            String traceId = LoggingUtil.getTraceId();
            String memberId = LoggingUtil.getMemberId();

            if (traceId != null) template.header("trace-id", traceId);
            if (memberId != null) template.header("member-id", memberId);
        };
    }
}
