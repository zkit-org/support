package org.zkit.support.starter.boot.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * 获取i18n资源文件
 * @author grant
 */
public class MessageUtils {

    public static String get(String code) {
        return get(code, null, null);
    }

    public static String get(String code, Object[] args) {
        return get(code, args, null);
    }

    public static String get(String code, String defaultMessage) {
        return get(code, null, defaultMessage);
    }

    public static String get(String code, Object[] args, String defaultMessage) {
        MessageSource messageSource = SpringUtils.getBean(MessageSource.class);
        return messageSource.getMessage(code, args, defaultMessage, LocaleContextHolder.getLocale());
    }

    public static String getLocale() {
        Locale locale = LocaleContextHolder.getLocale();
        return locale.toString().replaceAll("_", "-");
    }
}
