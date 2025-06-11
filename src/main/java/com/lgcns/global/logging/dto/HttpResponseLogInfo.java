package com.lgcns.global.logging.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.ContentCachingResponseWrapper;

public record HttpResponseLogInfo(String traceId, String responseBody, Integer responseStatus) {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static HttpResponseLogInfo from(ContentCachingResponseWrapper response)
            throws IOException {
        String traceId = MDC.get("traceId");
        String responseBody =
                getContent(response.getContentType(), response.getContentInputStream());
        Integer responseStatus = response.getStatus();

        return new HttpResponseLogInfo(traceId, responseBody, responseStatus);
    }

    private static String getContent(String contentType, InputStream inputStream)
            throws IOException {
        boolean visible =
                isVisible(
                        MediaType.valueOf(contentType == null ? "application/json" : contentType));
        if (visible) {
            byte[] content = StreamUtils.copyToByteArray(inputStream);
            if (content.length > 0) {
                return new String(content, 0, Math.min(content.length, 5120));
            } else {
                return "";
            }
        } else {
            return "BINARY";
        }
    }

    private static boolean isVisible(MediaType mediaType) {
        final List<MediaType> VISIBLE_TYPES =
                Arrays.asList(
                        MediaType.valueOf("text/*"),
                        MediaType.APPLICATION_FORM_URLENCODED,
                        MediaType.APPLICATION_JSON,
                        MediaType.APPLICATION_XML,
                        MediaType.valueOf("application/*+json"),
                        MediaType.valueOf("application/*+xml"),
                        MediaType.MULTIPART_FORM_DATA);

        return VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
    }

    public String toJson() {
        try {
            return objectMapper.writeValueAsString(this).replace("\\", "");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
