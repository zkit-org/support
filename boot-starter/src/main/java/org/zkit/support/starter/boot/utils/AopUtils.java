package org.zkit.support.starter.boot.utils;

import org.springframework.aop.framework.AopContext;

public class AopUtils {

    public static <T> T current(Class<T> clz) {
        Object o = AopContext.currentProxy();
        return clz.cast(o);
    }

}
