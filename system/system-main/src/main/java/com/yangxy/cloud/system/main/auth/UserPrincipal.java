package com.yangxy.cloud.system.main.auth;

import com.yangxy.cloud.system.main.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    public String getUserId() {
        return user.getId();
    }

    /**
     * TODO: 先空着
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 锁定逻辑我们用 Redis
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
