package org.zkit.support.redisson.starter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    String value() default ""; // 锁的名称
    long waitTime() default 30; // 获取锁等待时间，默认30秒
    long leaseTime() default 60; // 锁自动释放时间，默认60秒
    TimeUnit timeUnit() default TimeUnit.SECONDS; // 时间单位，默认秒
}
