package com.yangxy.cloud.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

/**
 * AI 接口限流拦截器
 *
 * 每个 IP 每分钟最多请求 5 次
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiRateLimitInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;

    // Redis Key 前缀
    private static final String RATE_LIMIT_KEY_PREFIX = "rate:limit:ai:";

    // 时间窗口：60 秒
    private static final long TIME_WINDOW = 60;

    // 最大请求次数：每分钟 5 次
    private static final int MAX_REQUESTS = 5;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取客户端 IP
        String clientIp = getClientIp(request);

        // 构建 Redis Key
        String rateLimitKey = RATE_LIMIT_KEY_PREFIX + clientIp;

        try {
            // 获取当前请求次数
            Integer count = (Integer) redisTemplate.opsForValue().get(rateLimitKey);

            if (count == null) {
                // 第一次请求，设置计数为 1
                redisTemplate.opsForValue().set(rateLimitKey, 1, TIME_WINDOW, TimeUnit.SECONDS);
                log.info("IP {} 首次请求 AI 接口", clientIp);
                return true;
            } else if (count < MAX_REQUESTS) {
                // 未超过限制，增加计数
                redisTemplate.opsForValue().increment(rateLimitKey);
                log.info("IP {} 请求 AI 接口，当前次数: {}/{}", clientIp, count + 1, MAX_REQUESTS);
                return true;
            } else {
                // 超过限制，拒绝请求
                log.warn("IP {} 请求频率过高，已拦截（{}/{} 次/分钟）", clientIp, count, MAX_REQUESTS);

                response.setStatus(429); // Too Many Requests
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(String.format(
                        "{\"error\":\"请求过于频繁\",\"message\":\"每分钟最多请求 %d 次，请稍后再试\",\"limit\":%d,\"window\":%d}",
                        MAX_REQUESTS, MAX_REQUESTS, TIME_WINDOW
                ));
                return false;
            }
        } catch (Exception e) {
            log.error("限流检查异常", e);
            // 异常时放行，不影响正常请求
            return true;
        }
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理的情况，第一个 IP 为真实 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}

