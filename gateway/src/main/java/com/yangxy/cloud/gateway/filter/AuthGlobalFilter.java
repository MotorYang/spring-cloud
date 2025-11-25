package com.yangxy.cloud.gateway.filter;

import com.yangxy.cloud.security.utils.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/25 06:29
 * 透传 Filter
 */
public class AuthGlobalFilter implements GlobalFilter, Ordered {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith("Bearer ")) {
            try {
                token = token.substring(7);
                // 解析出userId和userAccount
                String userId = String.valueOf(JwtUtils.getUserId(token));
                String userAccount = JwtUtils.getUserAccount(token);
                // 放入请求头，透穿给下游微服务
                ServerHttpRequest request = exchange.getRequest().mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Account", userAccount)
                        .build();
                return chain.filter(exchange.mutate().request(request).build());
            } catch (Exception e) {
                // Token 异常由 Security 处理，这里仅做透传尝试
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
