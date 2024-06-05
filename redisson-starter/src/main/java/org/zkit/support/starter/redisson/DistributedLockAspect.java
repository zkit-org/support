package org.zkit.support.starter.redisson;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

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
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Pointcut("@annotation(org.zkit.support.starter.redisson.DistributedLock)")
    public void annotationPointCut(){}

    @Around("annotationPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
        long waitTime = distributedLock.waitTime();
        long leaseTime = distributedLock.leaseTime();
        TimeUnit timeUnit = distributedLock.timeUnit();
        String[] lockValues = distributedLock.value();
        String[] keys = distributedLock.key();

        List<String> lockKeys = Stream.of(lockValues).map(lockValue -> {
            String key = keys.length > 0 ? keys[0] : "";
            String lockKey = StringUtils.isNotEmpty(key) ? evaluateExpression(key, joinPoint) : key;
            String redisKey = "lock:"+lockValue;
            if (StringUtils.isNotEmpty(lockKey)) {
                redisKey = "lock:"+lockValue + ":" + lockKey;
            }
            return redisKey;
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
                throw new RuntimeException("Failed to acquire lock");
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

    private String evaluateExpression(String expression, ProceedingJoinPoint point) {
        // 获取目标对象
        Object target = point.getTarget();
        // 获取方法参数
        Object[] args = point.getArgs();
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();

        EvaluationContext context = new MethodBasedEvaluationContext(target, method, args, parameterNameDiscoverer);
        Expression exp = spelExpressionParser.parseExpression(expression);
        return exp.getValue(context, String.class);
    }

}
