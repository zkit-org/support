package org.zkit.support.starter.boot.configurer;

import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.zkit.support.starter.boot.auth.CurrentUserArgumentResolver;
import org.zkit.support.starter.boot.service.SessionService;

import java.util.List;

@Configuration
public class WebMvcConfigurer implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {

    private SessionService sessionService;

    @Override
    public void configureMessageConverters(@NonNull List<HttpMessageConverter<?>> converters) {
        org.springframework.web.servlet.config.annotation.WebMvcConfigurer.super.configureMessageConverters(converters);
        converters.add(0, new FastJsonHttpMessageConverter());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        //注入用户信息
        argumentResolvers.add(new CurrentUserArgumentResolver(sessionService));
    }

    @Lazy
    @Autowired
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }
}
