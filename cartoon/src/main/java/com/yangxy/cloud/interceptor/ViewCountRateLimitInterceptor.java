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
 * 防刷拦截器
 * <p>
 * 功能：
 * 1. 限制同一 IP 对同一文章的访问频率
 * 2. 防止恶意刷浏览量
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountRateLimitInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;

    // Redis Key 前缀
    private static final String RATE_LIMIT_KEY_PREFIX = "rate:limit:view:";

    // 时间窗口：60 秒
    private static final long TIME_WINDOW = 60;

    // 最大请求次数：同一 IP 对同一文章 60 秒内最多访问 3 次
    private static final int MAX_REQUESTS = 3;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 只拦截增加浏览量的请求
        String uri = request.getRequestURI();
        if (!uri.contains("/increment-views")) {
            return true;
        }

        // 提取文章 ID
        String[] parts = uri.split("/");
        if (parts.length < 4) {
            return true;
        }
        String articleId = parts[parts.length - 2];

        // 获取客户端 IP
        String clientIp = getClientIp(request);

        // 构建 Redis Key
        String rateLimitKey = RATE_LIMIT_KEY_PREFIX + clientIp + ":" + articleId;

        try {
            // 获取当前访问次数
            Integer count = (Integer) redisTemplate.opsForValue().get(rateLimitKey);

            if (count == null) {
                // 第一次访问，设置计数为 1
                redisTemplate.opsForValue().set(rateLimitKey, 1, TIME_WINDOW, TimeUnit.SECONDS);
                return true;
            } else if (count < MAX_REQUESTS) {
                // 未超过限制，增加计数
                redisTemplate.opsForValue().increment(rateLimitKey);
                return true;
            } else {
                // 超过限制，拒绝请求
                log.warn("IP {} 对文章 {} 的访问频率过高，已拦截", clientIp, articleId);
                response.setStatus(429); // Too Many Requests
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"访问频率过高，请稍后再试\"}");
                return false;
            }
        } catch (Exception e) {
            log.error("防刷拦截器异常", e);
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
