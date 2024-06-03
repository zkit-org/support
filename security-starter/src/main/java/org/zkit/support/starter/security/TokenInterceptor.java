package org.zkit.support.starter.security;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.zkit.support.starter.boot.exception.ResultException;
import org.zkit.support.starter.boot.utils.RequestUtils;
import org.zkit.support.starter.security.annotation.CurrentUser;
import org.zkit.support.starter.security.annotation.PublicRequest;
import org.zkit.support.starter.security.configuration.AuthConfiguration;
import org.zkit.support.starter.security.entity.SessionUser;
import org.zkit.support.starter.security.service.SessionService;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    @Resource
    private SessionService sessionService;
    @Resource
    private AuthConfiguration configuration;
    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        log.info("request uri: {}", request.getRequestURI());
        SessionUser user = sessionService.get(RequestUtils.getToken(request));
        // 无论是否公开，尝试注入用户信息
        setUser(user, handler);

        // 开放权限
        boolean publicCheck = checkPublicAccess(request);
        if(publicCheck){
            return true;
        }

        // 公开的请求
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

        // 权限校验
        if (user != null) {
            if(configuration.getSu().equals(user.getUsername())){
                return true;
            }
            if(checkCommonAccess(request)){
                return true;
            }
            if(checkApis(user.getApis(), request)){
                return true;
            }
        }

        throw ResultException.unauthorized();
    }

    private boolean checkApis(List<SessionUser.Api> apis, HttpServletRequest request) {
        for (SessionUser.Api api : apis) {
            if (matcher.match(api.getPath(), request.getRequestURI()) && api.getMethod().equalsIgnoreCase(request.getMethod())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkPublicAccess(HttpServletRequest request) {
        String uri = request.getRequestURI();
        for (String publicApi : configuration.getPublicAccess()) {
            if (matcher.match(publicApi, uri)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkCommonAccess(HttpServletRequest request) {
        String uri = request.getRequestURI();
        for (String commonApi : configuration.getCommonAccess()) {
            if (matcher.match(commonApi, uri)) {
                return true;
            }
        }
        return false;
    }

    private void setUser(SessionUser user, @NonNull Object handler) {
        SessionHolder.set(user);
    }

    private boolean hasCurrentUserAnnotation(HandlerMethod handlerMethod){
        CurrentUser sessionUser = handlerMethod.getMethodAnnotation(CurrentUser.class);
        return sessionUser != null;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest httpServletRequest, @NonNull HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        SessionHolder.remove();
    }

}
