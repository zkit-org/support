package org.zkit.support.cloud.starter.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.zkit.support.cloud.starter.auth.annotation.PublicRequest;

import java.util.Objects;

@Component
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull  HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            PublicRequest methodAnnotation = handlerMethod.getMethodAnnotation(PublicRequest.class);
            if (Objects.nonNull(methodAnnotation)) {
                log.info("Method @PublicRequest");
                return true;
            }
            Class<?> clazz = handlerMethod.getBeanType();
            if (Objects.nonNull(AnnotationUtils.findAnnotation(clazz, PublicRequest.class))) {
                log.info("Class @PublicRequest");
                return true;
            }
        }
        String uri = request.getRequestURI();
        log.info("start TokenInterceptor preHandle, {}", uri);
        return true;
    }

}
