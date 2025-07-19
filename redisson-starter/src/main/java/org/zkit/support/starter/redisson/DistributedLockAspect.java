package org.zkit.support.starter.redisson;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.zkit.support.starter.boot.exception.ResultException;
import org.zkit.support.starter.boot.utils.SpelExpressionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Aspect
@Component
@Slf4j
public class DistributedLockAspect {

    @Resource
    private RedissonClient redissonClient;

    @Pointcut("@annotation(org.zkit.support.starter.redisson.DistributedLock)")
    public void annotationPointCut(){}

    @Around("annotationPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
        long waitTime = distributedLock.waitTime();
        long leaseTime = distributedLock.leaseTime();
        boolean el = distributedLock.el();
        TimeUnit timeUnit = distributedLock.timeUnit();
        String[] lockValues = distributedLock.value();

        List<String> lockKeys = Stream.of(lockValues).map(lockValue -> {
            String lockKey = el ? SpelExpressionUtils.evaluateExpression(lockValue, joinPoint, String.class) : lockValue;
            return "lock:"+lockKey;
        }).toList();
        log.info("Lock keys: {}", lockKeys);

        RLock lock = getMultiLock(lockKeys);
        try {
            if (lock.tryLock(waitTime, leaseTime, timeUnit)) {
                try {
                    return joinPoint.proceed();
                } finally {
                    lock.unlock();
                }
            } else {
                throw ResultException.busy();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("The process of acquiring the lock was interrupted", e);
        }
    }

    private RLock getMultiLock(List<String> lockKeys) {
        List<RLock> locks = lockKeys.stream().map(redissonClient::getLock).toList();
        RLock[] lockArray = locks.toArray(new RLock[0]);
        log.info("Get multi lock: {}", Arrays.toString(lockArray));
        return redissonClient.getMultiLock(lockArray);
    }

}
