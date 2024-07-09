package org.zkit.support.starter.gateway.filter;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.zkit.support.starter.gateway.configuration.InternalConfiguration;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InternalFilter implements GlobalFilter, Ordered {

    private final AntPathMatcher matcher = new AntPathMatcher();
    @Resource
    private InternalConfiguration configuration;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if(!configuration.getEnabled()) {
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        boolean matched = matcher.match(configuration.getPattern(), request.getURI().getPath());
        if(matched) {
            log.info("InternalFilter, {}", request.getURI());
            //3.1设置状态码(黑马出品,必出精品)
            response.setStatusCode(HttpStatus.FORBIDDEN);

            //3.2封装返回数据
            Map<String, Object> map = new HashMap<>();
            map.put("code", HttpStatus.FORBIDDEN.value());
            map.put("success", false);
            map.put("message", "Internal interface is not accessible");

            //3.3作JSON转换
            byte[] bytes = JSON.toJSONString(map).getBytes(StandardCharsets.UTF_8);

            //3.4调用bufferFactory方法,生成DataBuffer对象
            DataBuffer buffer = response.bufferFactory().wrap(bytes);

            response.getHeaders().set("Content-Type", "application/json");

            //4.调用Mono中的just方法,返回要写给前端的JSON数据
            return response.writeWith(Mono.just(buffer));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
