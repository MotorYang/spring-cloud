package com.yangxy.cloud.system.main.utils;

import com.yangxy.cloud.system.main.auth.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {
        // 私有构造，防止实例化
    }

    /**
     * 获取当前 Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前登录的 UserPrincipal
     */
    public static UserPrincipal getCurrentUser() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal user) {
                return user;
            }
        }
        return null;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getUsername() {
        UserPrincipal user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 获取当前登录用户ID
     */
    public static String getUserId() {
        UserPrincipal user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * 判断当前用户是否已登录
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }

}
