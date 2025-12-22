package com.yangxy.cloud.gateway.filter;

import jakarta.annotation.Resource;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 登录接口IP限流
 */
@Component
public class LoginIpRateLimitFilter implements GlobalFilter, Ordered {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (!"/system/auth/login".equals(path)) {
            return chain.filter(exchange);
        }
        String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        String key = "rate_limit:login:" + ip;
        Long count = stringRedisTemplate.opsForValue().increment(key);
        if (count == 1L) {
            stringRedisTemplate.expire(key, Duration.ofMinutes(1));
        }

        if (count > 10L) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
