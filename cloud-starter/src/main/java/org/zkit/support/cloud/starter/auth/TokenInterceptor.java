package org.zkit.support.cloud.starter.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.zkit.support.cloud.starter.auth.annotation.PublicRequest;
import org.zkit.support.cloud.starter.auth.annotation.CurrentUser;
import org.zkit.support.cloud.starter.entity.SessionUser;
import org.zkit.support.cloud.starter.service.SessionService;

import java.util.Objects;

@Component
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    private SessionService sessionService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String uri = request.getRequestURI();
        log.info("start TokenInterceptor preHandle, {}", uri);
        setUser(request, handler);
        if (handler instanceof HandlerMethod handlerMethod) {
            PublicRequest methodAnnotation = handlerMethod.getMethodAnnotation(PublicRequest.class);
            if (Objects.nonNull(methodAnnotation)) {
                return true;
            }
            Class<?> clazz = handlerMethod.getBeanType();
            if (Objects.nonNull(AnnotationUtils.findAnnotation(clazz, PublicRequest.class))) {
                return true;
            }
        }
        return true;
    }

    private void setUser(@NonNull HttpServletRequest request, @NonNull Object handler) {
        if(handler instanceof HandlerMethod && hasCurrentUserAnnotation((HandlerMethod) handler)){
            //根据token获取当前用户信息
            SessionUser user = new SessionUser();
            //放入到ThreadLocal中，一定要在afterCompletion方法中将ThreadLocal中的UserInfo数据删除，不然会造成内存泄漏，最终导致OOM
            SessionHolder.set(user);
        }
    }

    private boolean hasCurrentUserAnnotation(HandlerMethod handlerMethod){
        CurrentUser sessionUser = handlerMethod.getMethodAnnotation(CurrentUser.class);
        return sessionUser != null;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest httpServletRequest, @NonNull HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        SessionHolder.remove();
    }

    @Autowired
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }
}
