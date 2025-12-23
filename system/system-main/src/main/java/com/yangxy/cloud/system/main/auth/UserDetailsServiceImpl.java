package com.yangxy.cloud.system.main.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yangxy.cloud.system.main.user.dao.UserDAO;
import com.yangxy.cloud.system.main.user.entity.User;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserDAO userDAO;
    @Resource
    private LoginPreCheckService loginPreCheckService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        loginPreCheckService.checkLocked(username);

        User user = userDAO.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        );

        if (user == null) {
            throw new UsernameNotFoundException("Account Or Password failed");
        }

        return new UserPrincipal(user);
    }
}
