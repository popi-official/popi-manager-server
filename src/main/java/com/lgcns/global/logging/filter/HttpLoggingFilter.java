package com.lgcns.global.logging.filter;

import static org.springframework.web.multipart.support.MultipartResolutionDelegate.isMultipartRequest;

import com.lgcns.global.logging.dto.HttpRequestLogInfo;
import com.lgcns.global.logging.dto.HttpResponseLogInfo;
import com.lgcns.global.logging.filter.wrapper.RequestWrapper;
import com.lgcns.global.logging.filter.wrapper.ResponseWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        MDC.put("traceId", UUID.randomUUID().toString());
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        } else if (isMultipartRequest(request)) {
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(
                    new RequestWrapper(request), new ResponseWrapper(response), filterChain);
        }
        MDC.clear();
    }

    protected void doFilterWrapped(
            RequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            logRequest(request);
            filterChain.doFilter(request, response);
        } finally {
            logResponse(response);
            response.copyBodyToResponse();
        }
    }

    private static void logRequest(RequestWrapper request) {
        HttpRequestLogInfo httpLogInfo = HttpRequestLogInfo.from(request);
        log.info(httpLogInfo.toJson());
    }

    private static void logResponse(ContentCachingResponseWrapper response) throws IOException {
        HttpResponseLogInfo httpLogInfo = HttpResponseLogInfo.from(response);
        log.info(httpLogInfo.toJson());
    }
}
