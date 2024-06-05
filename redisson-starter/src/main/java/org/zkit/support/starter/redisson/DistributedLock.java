package org.zkit.support.starter.redisson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    String[] value() default {""}; // 锁的名称
    long waitTime() default -1L; // 等待时间
    long leaseTime() default -1L; // -1表示永不过期
    boolean el() default true; // 是否使用SpEL表达式
    TimeUnit timeUnit() default TimeUnit.SECONDS; // 时间单位，默认秒
}
