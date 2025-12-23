package com.yangxy.cloud.security.web.interceptor;

import com.yangxy.cloud.security.common.context.UserContext;
import com.yangxy.cloud.security.common.vo.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * 用户上下文拦截器（供微服务使用）
 */
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIdStr = request.getHeader("X-User-Id");
        String username = request.getHeader("X-Username");
        String roles = request.getHeader("X-User-Roles");
        String permissions = request.getHeader("X-User-Permissions");
        if (userIdStr != null) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(userIdStr);
            userInfo.setUsername(username);
            if (roles != null && !roles.isEmpty()) {
                userInfo.setRoles(Arrays.asList(roles.split(",")));
            }
            if (permissions != null && !permissions.isEmpty()) {
                userInfo.setPermissions(Arrays.asList(permissions.split(",")));
            }
            UserContext.setUser(userInfo);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        // 清理ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
}
