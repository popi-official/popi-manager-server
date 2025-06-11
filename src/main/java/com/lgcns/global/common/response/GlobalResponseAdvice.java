package com.lgcns.global.common.response;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(
        basePackages = {
            "com.lgcns.domain.auth.externalApi",
            "com.lgcns.domain.congestionStats.externalApi",
            "com.lgcns.domain.conversionStats.externalApi",
            "com.lgcns.domain.entrance.externalApi",
            "com.lgcns.domain.image.externalApi",
            "com.lgcns.domain.item.externalApi",
            "com.lgcns.domain.itemAnalysis.externalApi",
            "com.lgcns.domain.manager.externalApi",
            "com.lgcns.domain.notification.externalApi",
            "com.lgcns.domain.paymentStats.externalApi",
            "com.lgcns.domain.popup.externalApi",
            "com.lgcns.domain.reservation.externalApi",
            "com.lgcns.domain.reservationStats.externalApi",
            "com.lgcns.domain.survey.externalApi",
            "com.lgcns.domain.visitorStats.externalApi",
            "com.lgcns.domain.orderItem.externalApi"
        })
public class GlobalResponseAdvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        HttpServletResponse servletResponse =
                ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();
        HttpStatus resolve = HttpStatus.resolve(status);

        if (resolve == null || body instanceof String) {
            return body;
        }

        if (resolve.is2xxSuccessful()) {
            return GlobalResponse.success(status, body);
        }

        return body;
    }
}
