package com.lgcns.global.logging.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgcns.global.logging.filter.wrapper.RequestWrapper;
import org.slf4j.MDC;

public record HttpRequestLogInfo(
        String traceId,
        String requestMethod,
        String requestUri,
        String xAmznTraceId,
        String userAgent) {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static HttpRequestLogInfo from(RequestWrapper requestWrapper) {
        String queryString = requestWrapper.getQueryString();
        String traceId = MDC.get("traceId");
        String requestMethod = requestWrapper.getMethod();
        String requestUri =
                requestWrapper.getRequestURI() + (queryString == null ? "" : "?" + queryString);
        String xAmznTraceId = requestWrapper.getHeader("x-amzn-trace-id");
        String userAgent = requestWrapper.getHeader("user-agent");

        return new HttpRequestLogInfo(traceId, requestMethod, requestUri, xAmznTraceId, userAgent);
    }

    public String toJson() {
        try {
            return objectMapper.writeValueAsString(this).replace("\\", "");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
