package org.zkit.support.starter.throttler.aspect;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.zkit.support.starter.boot.exception.ResultException;
import org.zkit.support.starter.throttler.annotation.Throttler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class ThrottlerAspect {

    private static void initFlowRule(String resourceName, int limitCount) {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        //设置受保护的资源
        rule.setResource(resourceName);
        //设置流控规则  QPS
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        //设置受保护的资源阈值
        rule.setCount(limitCount);
        rules.add(rule);
        //加载配置好的规则
        FlowRuleManager.loadRules(rules);
    }

    @Pointcut(value = "@annotation(org.zkit.support.starter.throttler.annotation.Throttler)")
    public void rateLimit() {}

    @Around("rateLimit()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //1、获取当前的调用方法
        Method currentMethod = getCurrentMethod(joinPoint);
        if (Objects.isNull(currentMethod)) {
            return null;
        }
        //2、从方法注解定义上获取限流的类型
        String resourceName = currentMethod.getAnnotation(Throttler.class).value();
        if (StringUtils.isEmpty(resourceName)) {
            throw new RuntimeException("Throttler: Resource name is empty");
        }
        int limitCount = currentMethod.getAnnotation(Throttler.class).limit();
        initFlowRule(resourceName, limitCount);

        Entry entry = null;
        Object result = null;
        try {
            entry = SphU.entry(resourceName);
            result = joinPoint.proceed();
        } catch (BlockException ex) {
            throw ResultException.busy();
        } catch (Throwable e) {
            Tracer.traceEntry(e, entry);
            throw e;
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
        return result;
    }

    private Method getCurrentMethod(JoinPoint joinPoint) {
        Method[] methods = joinPoint.getTarget().getClass().getMethods();
        Method target = null;
        for (Method method : methods) {
            if (method.getName().equals(joinPoint.getSignature().getName())) {
                target = method;
                break;
            }
        }
        return target;
    }

}
