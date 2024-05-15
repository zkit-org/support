package org.zkit.support.starter.boot.filter;

import io.netty.handler.codec.http.HttpMethod;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class ApmHttpInfoFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 解决请求体只能被读取一次的问题，先缓存起来
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        // 解决响应体只能被读取一次的问题，先缓存起来
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(requestWrapper, responseWrapper);

        try {
            // 请求方法
            String method = request.getMethod();
            // 请求内容类型
            String requestContentType = request.getContentType();

            // 请求体信息针对于 POST 和 PUT 类型
            if ((HttpMethod.POST.name().equals(method) || HttpMethod.PUT.name().equals(method)) && requestContentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
                // 获取请求体信息
                String requestBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
                if (StringUtils.hasLength(requestBody)) {
                    // 往 SkyWalking 中添加 Tag 信息（核心）
                    ActiveSpan.tag("request.body", requestBody);
                }
            }

            // 响应内容类型
            String responseContentType = response.getContentType();
            if (responseContentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
                // 获取返回值 body 信息
                String responseBody = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
                if (StringUtils.hasLength(responseBody)) {
                    // 往 SkyWalking 中添加 Tag 信息（核心）
                    ActiveSpan.tag("response.body", responseBody);
                }
            }
        } catch (Exception ex) {
            log.error("拦截 Http 请求添加 SkyWalking Tag 信息异常", ex);
        } finally {
            // 将响应信息写回到输出流，解决返回的响应体是空的问题
            responseWrapper.copyBodyToResponse();
        }
    }
}
