package com.yangxy.cloud.system.main.auth;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Component;

@Component
public class LoginPreCheckService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void checkLocked(String username) {
        String key = "login_fail:" + username;
        String count = stringRedisTemplate.opsForValue().get(key);

        if (count != null && Integer.parseInt(count) >= 5) {
            throw new LockedException("账号已锁定");
        }
    }

}
