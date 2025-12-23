package com.yangxy.cloud.security.common.context;

import com.yangxy.cloud.security.common.vo.UserInfo;

import java.util.List;

/**
 * 用户上下文
 */
public class UserContext {

    private static final ThreadLocal<UserInfo> userThreadLocal = new ThreadLocal<>();

    public static void setUser(UserInfo userInfo) {
        userThreadLocal.set(userInfo);
    }

    public static UserInfo getCurrentUser() {
        return userThreadLocal.get();
    }

    public static String getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    public static String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    public static List<String> getCurrentUserRoles() {
        return getCurrentUser().getRoles();
    }

    public static boolean hasRole(String role) {
        List<String> roles = getCurrentUserRoles();
        return roles != null && roles.contains(role);
    }

    public static void clear() {
        userThreadLocal.remove();
    }
}
