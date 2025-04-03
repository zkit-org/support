package org.zkit.support.starter.tracing.advice;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import static org.zkit.support.starter.tracing.TracingConst.*;

@ControllerAdvice
@Slf4j
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(@NotNull MethodParameter returnType, @NotNull Class converterType){
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body, @NotNull MethodParameter returnType,
            @NotNull MediaType selectedContentType, @NotNull Class selectedConverterType,
            @NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response) {
        String traceId = MDC.get(TRACE_ID);
        String spanId = MDC.get(SPAN_ID);
        if(traceId == null || spanId == null){
            return body;
        }
        response.getHeaders().add(HEADER_NAME, traceId + "-" + spanId);
        return body;
    }
}
