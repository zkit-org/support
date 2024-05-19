package org.zkit.support.starter.cloud.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.jboss.logging.MDC;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        template.header("x-trace-id", (String) MDC.get("traceId"));
    }

}
