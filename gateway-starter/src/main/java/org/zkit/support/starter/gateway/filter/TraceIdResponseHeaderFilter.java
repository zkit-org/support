package org.zkit.support.starter.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.apache.skywalking.apm.toolkit.webflux.WebFluxSkyWalkingOperators;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class TraceIdResponseHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = WebFluxSkyWalkingOperators.continueTracing(exchange, TraceContext::traceId);
        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .header("x-trace-id", traceId)
                .build();
        exchange.mutate().request(request).build();
        exchange.getResponse().getHeaders().set("x-trace-id", traceId);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

