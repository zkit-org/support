package org.zkit.support.starter.cloud.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.service.GenericService;
import org.zkit.support.starter.boot.exception.ResultException;

import java.lang.reflect.Method;

@Activate(group = "provider")
@Slf4j
public class DubboExceptionFilter implements Filter, Filter.Listener {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        return invoker.invoke(invocation);
    }

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
        if (appResponse.hasException() && GenericService.class != invoker.getInterface()) {
            try {
                Throwable exception = appResponse.getException();

                // directly throw if it's checked exception
                if (!(exception instanceof RuntimeException) && (exception instanceof Exception)) {
                    return;
                }

                // directly throw if the exception appears in the signature
                try {
                    Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                    Class<?>[] exceptionClasses = method.getExceptionTypes();
                    for (Class<?> exceptionClass : exceptionClasses) {
                        if (exception.getClass().equals(exceptionClass)) {
                            return;
                        }
                    }
                } catch (NoSuchMethodException e) {
                    return;
                }

                // for the exception not found in method's signature, print ERROR message in server's log.
                log.error("Got unchecked and undeclared exception which called by {}. service: {}, method: {}, exception: {}: {}", RpcContext.getServiceContext().getRemoteHost(), invoker.getInterface().getName(), invocation.getMethodName(), exception.getClass().getName(), exception.getMessage());

                // directly throw if exception class and interface class are in the same jar file.
                String serviceFile = ReflectUtils.getCodeBase(invoker.getInterface());
                String exceptionFile = ReflectUtils.getCodeBase(exception.getClass());
                if (serviceFile == null || exceptionFile == null || serviceFile.equals(exceptionFile)) {
                    return;
                }
                // directly throw if it's JDK exception
                String className = exception.getClass().getName();
                if (className.startsWith("java.") || className.startsWith("javax.")) {
                    return;
                }
                // directly throw if it's dubbo exception
                if (exception instanceof RpcException) {
                    return;
                }

                // 返回自定义异常，我们项目中自定义的异常，根据自己的情况进行添加
                if (exception instanceof ResultException) {
                    appResponse.setException(exception);
                    return;
                }

                // otherwise, wrap with RuntimeException and throw back to the client
                appResponse.setException(new RuntimeException(StringUtils.toString(exception)));
            } catch (Throwable e) {
                log.warn("Fail to ExceptionFilter when called by {}. service: {}, method: {}, exception: {}: {}", RpcContext.getServiceContext().getRemoteHost(), invoker.getInterface().getName(), invocation.getMethodName(), e.getClass().getName(), e.getMessage());
            }
        }

    }

    @Override
    public void onError(Throwable e, Invoker<?> invoker, Invocation invocation) {
        log.error("Got unchecked and undeclared exception which called by {}. service: {}, method: {}, exception: {}: {}", RpcContext.getServiceContext().getRemoteHost(), invoker.getInterface().getName(), invocation.getMethodName(), e.getClass().getName(), e.getMessage());
    }

}
