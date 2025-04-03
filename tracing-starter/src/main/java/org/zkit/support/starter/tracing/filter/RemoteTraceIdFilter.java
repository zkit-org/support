package org.zkit.support.starter.tracing.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.MDC;

import static org.zkit.support.starter.tracing.TracingConst.*;

@Activate(group = {CommonConstants.CONSUMER, CommonConstants.PROVIDER}, order = -30000)
@Slf4j
public class RemoteTraceIdFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // 消费者
        RpcServiceContext currentServiceContext = RpcContext.getCurrentServiceContext();
        boolean consumerSide = currentServiceContext.isConsumerSide();
        if (consumerSide) {
            String traceId = MDC.get(TRACE_ID);
            String spanId = MDC.get(SPAN_ID);
            //消费者 将trace_id（业务流水号） set至上下文中
            RpcContext.getClientAttachment().setAttachment(TRACE_ID, traceId);
            RpcContext.getClientAttachment().setAttachment(SPAN_ID, spanId);
        } else {
            // 服务提供者
            String traceId = RpcContext.getServerAttachment().getAttachment(TRACE_ID);
            String spanId = RpcContext.getServerAttachment().getAttachment(SPAN_ID);
            //slf4j 中设置了日志打印格式用作日志链路追踪
            MDC.put(TRACE_ID, traceId);
            MDC.put(SPAN_ID, spanId);
        }
        try {
            return invoker.invoke(invocation);
        } finally {
            if (RpcContext.getCurrentServiceContext().isProviderSide()) {
                MDC.remove(TRACE_ID);
                MDC.remove(SPAN_ID);
            }else{
                RpcContext.getClientAttachment().removeAttachment(TRACE_ID);
                RpcContext.getClientAttachment().removeAttachment(SPAN_ID);
            }
        }
    }
}
