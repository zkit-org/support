package org.zkit.support.starter.security.configurer;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.zkit.support.starter.security.CurrentUserArgumentResolver;
import org.zkit.support.starter.security.TokenInterceptor;
import org.zkit.support.starter.security.configuration.AuthConfiguration;
import org.zkit.support.starter.security.service.SessionService;

import java.util.List;

@Configuration
@Slf4j
public class WebMvcConfigurer implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {

    @Resource
    private SessionService sessionService;
    @Resource
    private TokenInterceptor tokenInterceptor;
    @Resource
    private AuthConfiguration configuration;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        //注入用户信息
        argumentResolvers.add(new CurrentUserArgumentResolver(sessionService));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> publicAccess = configuration.getPublicAccess();
        log.info("publicAccess: {}", publicAccess);
        registry.addInterceptor(tokenInterceptor)
                .excludePathPatterns(publicAccess)
                .addPathPatterns("/**");
    }

}
