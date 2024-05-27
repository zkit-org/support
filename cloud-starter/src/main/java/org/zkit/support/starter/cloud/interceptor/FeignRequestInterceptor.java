package org.zkit.support.starter.cloud.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.jboss.logging.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

@Configuration
public class FeignRequestInterceptor implements RequestInterceptor {

    private static String getLocale() {
        Locale locale = LocaleContextHolder.getLocale();
        return locale.toString().replaceAll("_", "-");
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header("x-trace-id", (String) MDC.get("traceId"));
        template.header("Accept-Language", getLocale());
    }

}
