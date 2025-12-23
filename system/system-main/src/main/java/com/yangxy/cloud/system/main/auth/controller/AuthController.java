package com.yangxy.cloud.system.main.auth.controller;

import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yangxy.cloud.common.exception.BusinessException;
import com.yangxy.cloud.common.response.RestResult;
import com.yangxy.cloud.security.common.utils.JwtUtils;
import com.yangxy.cloud.security.common.vo.LoginRequest;
import com.yangxy.cloud.system.main.auth.UserPrincipal;
import com.yangxy.cloud.system.main.user.dao.UserDAO;
import com.yangxy.cloud.system.main.user.dto.UserDTO;
import com.yangxy.cloud.system.main.user.entity.User;
import com.yangxy.cloud.system.main.user.mapper.UserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/25 06:41
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private UserDAO userDAO;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final long MAX_FAIL = 5; // 最大失败次数
    private static final long LOCK_MINUTES = 10; // 锁定时间

    @PostMapping("/login")
    public RestResult<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {

        String username = loginRequest.getUsername();
        String key = "login_fail:" + username;
        // 检查账号是否被锁定
        String failCountStr = stringRedisTemplate.opsForValue().get(key);
        long failCount = failCountStr != null ? Long.parseLong(failCountStr) : 0;
        if (failCount >= MAX_FAIL) {
            return RestResult.error("账号已锁定 " + LOCK_MINUTES + " 分钟");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            // 登录成功，清除失败计数
            stringRedisTemplate.delete(key);
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            JwtUtils.TokenResponse token = JwtUtils.generateTokens(principal.getUsername(), principal.getUserId());
            return RestResult.success(Map.of(
                    "token", token.getToken(),
                    "refreshToken", token.getRefreshToken(),
                    "expiresIn", token.getExpiresIn()
            ));
        } catch (AuthenticationException e) {
            // 登录失败，增加失败次数
            failCount = stringRedisTemplate.opsForValue().increment(key);
            if (failCount == 1L) {
                stringRedisTemplate.expire(key, LOCK_MINUTES, TimeUnit.MINUTES);
            }

            String msg = failCount >= MAX_FAIL ?
                    "账号已锁定 " + LOCK_MINUTES + " 分钟" :
                    "账号或密码错误";

            return RestResult.error(msg);
        }
    }

    @PostMapping("/register")
    public RestResult<UserDTO> register(@RequestBody UserDTO userDTO) {
        // 1. Validate
        if (StringUtils.isBlank(userDTO.getUsername())) {
            throw new BusinessException("Account cannot be empty!");
        }
        if (StringUtils.isBlank(userDTO.getPassword())) {
            throw new BusinessException("Password cannot be empty!");
        }
        // 2. Check if user exists
        User exitsUser = userDAO.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, userDTO.getUsername()));
        if (exitsUser != null) {
            throw new BusinessException("User already exists!");
        }
        // 3. Register user
        User user = UserMapper.INSTANCE.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDAO.insert(user);
        // 4. Return the registered user
        User userEntity = userDAO.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, userDTO.getUsername()));
        if (userEntity == null) {
            throw new BusinessException("User registration failed!");
        }
        return RestResult.success(UserMapper.INSTANCE.toDTO(userEntity));
    }

    @GetMapping("/logout")
    public RestResult<Void> logout() {
        log.warn("用户登出");
        return RestResult.success(null);
    }

}
