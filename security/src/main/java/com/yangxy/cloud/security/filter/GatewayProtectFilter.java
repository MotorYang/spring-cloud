package com.yangxy.cloud.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/25 09:06
 * 用于在代码层面阻止用户不经过网关直接调用微服务
 */
public class GatewayProtectFilter implements Filter {

    private static final String GATEWAY_TOKEN_HEADER = "X-Gateway-Token";
    private static final String GATEWAY_TOKEN_VALUE = "bKAgyPdf0jz9UvNAD1NwQwQc1ewWzreXHK732quM5UY=";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 1. 获取请求头
        String token = request.getHeader(GATEWAY_TOKEN_HEADER);
        // 2. 校验暗号
        if (!StringUtils.hasText(token) || !GATEWAY_TOKEN_VALUE.equals(token)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"code\":403, \"msg\":\"禁止直接访问内部服务，请走网关！\"}");
            return;
        }
        // 3. 校验通过，放行
        filterChain.doFilter(request, response);
    }
}
