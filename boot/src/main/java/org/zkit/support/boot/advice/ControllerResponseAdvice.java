package org.zkit.support.boot.advice;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.zkit.support.boot.entity.Result;
import org.zkit.support.boot.exception.ResultException;

@ControllerAdvice
@Slf4j
@NonNullApi
public class ControllerResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        String path = request.getURI().getPath();
        if(path.startsWith("/inner")) return body;
        if(body instanceof Result){
            return body;
        }
        return Result.success(body);
    }

    @ExceptionHandler(value = ResultException.class)
    @ResponseBody
    public Result<Object> resultExceptionHandler(ResultException e){
        log.error(e.toString());
        return Result.error(e.getCode(), e.getMessage(), e.getData());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result<Object> exceptionHandler(Exception e){
        log.error(e.toString());
        return Result.error("Server error");
    }
}
