package org.zkit.support.starter.cloud.dubbo.filter;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.zkit.support.starter.boot.utils.MessageUtils;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;

@Activate(group = {"provider", "consumer"})
@Slf4j
public class DubboLanguageFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (RpcContext.getServiceContext().getUrl() != null) {
            if (RpcContext.getServiceContext().isConsumerSide()) {
                // spring 获取当前语言
                String language = MessageUtils.getLocale();
                RpcContext.getServiceContext().setAttachment("language", language);
                log.info("DubboLanguageFilter: consumer set language: {}", language);
            } else if (RpcContext.getServiceContext().isProviderSide()) {
                // 获取请求头中的语言
                String language = RpcContext.getServiceContext().getAttachment("language");
                if (language != null) {
                    // 设置当前语言
                    LocaleContextHolder.setLocale(Locale.forLanguageTag(language));
                    log.info("DubboLanguageFilter: provider set language: {}", language);
                }
            }
        }
        return invoker.invoke(invocation);
    }

}
