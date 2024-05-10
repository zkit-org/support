package org.zkit.support.starter.boot.advice;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.zkit.support.starter.boot.configuration.ResponseAdviceConfiguration;
import org.zkit.support.starter.boot.entity.Result;
import org.zkit.support.starter.boot.exception.ResultException;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
@NonNullApi
public class ControllerResponseAdvice implements ResponseBodyAdvice<Object> {

    private ResponseAdviceConfiguration configuration;
    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    private boolean isExclude(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return configuration.getExclude().stream().anyMatch(pattern -> matcher.match(pattern, path));
    }

    @Override
    public Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if(isExclude(request)){
            return body;
        }
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

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> validationHandler(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        if (!bindingResult.hasErrors()) {
            return Result.error(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null);
        }
        String res2 = bindingResult.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Result.error(HttpStatus.BAD_REQUEST.value(), res2, null);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result<Object> exceptionHandler(Exception e){
        log.error(e.toString());
        e.printStackTrace();
        return Result.error("Server error");
    }

    @Autowired
    public void setConfiguration(ResponseAdviceConfiguration configuration) {
        this.configuration = configuration;
    }
}