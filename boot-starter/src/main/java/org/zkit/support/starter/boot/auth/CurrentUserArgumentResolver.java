package org.zkit.support.starter.boot.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.zkit.support.starter.boot.auth.annotation.CurrentUser;
import org.zkit.support.starter.boot.entity.SessionUser;
import org.zkit.support.starter.boot.service.SessionService;
import org.zkit.support.starter.boot.utils.RequestUtils;

public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final SessionService sessionService;

    public CurrentUserArgumentResolver(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    /**
     * 入参筛选
     *
     * @param methodParameter 参数集合
     * @return 格式化后的参数
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(CurrentUser.class) && methodParameter.getParameterType().equals(SessionUser.class);
    }

    /**
     * @param methodParameter       入参集合
     * @param modelAndViewContainer model 和 view
     * @param nativeWebRequest      web相关
     * @param webDataBinderFactory  入参解析
     * @return 包装对象
     */
    @Override
    public Object resolveArgument(
            MethodParameter methodParameter,
            ModelAndViewContainer modelAndViewContainer,
            @NonNull NativeWebRequest nativeWebRequest,
            WebDataBinderFactory webDataBinderFactory
    ) {
        CurrentUser currentUser = methodParameter.getParameterAnnotation(CurrentUser.class);
        if(currentUser == null) return null;
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        return request == null ? null : sessionService.get(RequestUtils.getToken(request));
    }
}