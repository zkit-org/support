package org.zkit.support.starter.boot.configurer;

import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class WebMvcConfigurer implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {

    @Override
    public void configureMessageConverters(@NonNull List<HttpMessageConverter<?>> converters) {
        org.springframework.web.servlet.config.annotation.WebMvcConfigurer.super.configureMessageConverters(converters);
        converters.add(0, new FastJsonHttpMessageConverter());
    }

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
