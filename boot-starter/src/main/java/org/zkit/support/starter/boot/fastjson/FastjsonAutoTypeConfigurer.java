package org.zkit.support.starter.boot.fastjson;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import com.alibaba.fastjson2.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class FastjsonAutoTypeConfigurer implements ApplicationContextAware, InitializingBean {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private ApplicationContext applicationContext;

    public static String[] fastjsonTypePrefixes;

    @Override
    public void afterPropertiesSet() {
        // 查找带注解的主类
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(FastjsonAutoTypePackages.class);
        log.info("FastjsonAutoTypeConfigurer beans: {}", JSONObject.toJSONString(beans));
        for (Object bean : beans.values()) {
            FastjsonAutoTypePackages ann = bean.getClass().getAnnotation(FastjsonAutoTypePackages.class);
            log.info("FastjsonAutoTypeConfigurer ann: {}", JSONObject.toJSONString(ann));
            if (ann != null) {
                fastjsonTypePrefixes = ann.value();
                log.info("FastjsonAutoTypeConfigurer fastjsonTypePrefixes: {}", JSONObject.toJSONString(fastjsonTypePrefixes));
            }
        }
    }

}
