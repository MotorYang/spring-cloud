package com.yangxy.cloud.system.main.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yangxy.cloud.common.exception.BusinessException;
import com.yangxy.cloud.common.response.RestResult;
import com.yangxy.cloud.security.utils.JwtUtils;
import com.yangxy.cloud.security.vo.LoginRequest;
import com.yangxy.cloud.system.main.user.dto.User;
import com.yangxy.cloud.system.main.user.entity.UserEntity;
import com.yangxy.cloud.system.main.user.mapper.UserMapStruct;
import com.yangxy.cloud.system.main.user.mapper.UserMapper;
import com.yangxy.cloud.system.main.user.vo.UserVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    private UserMapper userMapper;
    @Resource
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public RestResult<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        // 1. Query user
        UserEntity user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getAccount, loginRequest.getAccount()));
        if (user == null) {
            throw new BusinessException(11001, "Account Or Password failed");
        }
        // 2. Validate password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException(11001, "Account Or Password failed");
        }
        // 3. Generate token
        JwtUtils.TokenResponse token = JwtUtils.generateTokens(user.getAccount(), user.getId());
        return RestResult.success(Map.of(
                "token", token.getToken(),
                "refreshToken", token.getRefreshToken(),
                "expiresIn", token.getExpiresIn(),
                "user", warp(user),
                // TODO: 角色先空着
                "roles", new String[]{"admin"}
        ));
    }

    @PostMapping("/register")
    public RestResult<UserVO> register(@RequestBody UserVO userVO) {
        // 1. Validate
        if (userVO.getAccount().isBlank()) {
            throw new BusinessException("Account cannot be empty!");
        }
        if (userVO.getPassword().isBlank()) {
            throw new BusinessException("Password cannot be empty!");
        }
        // 2. Check if user exists
        UserEntity exitsUser = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getAccount, userVO.getAccount())
        );
        if (exitsUser != null) {
            throw new BusinessException("User already exists!");
        }
        // 3. Register user
        User user = UserMapStruct.INSTANCE.userVoToUser(userVO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(UserMapStruct.INSTANCE.userToUserEntity(user));
        // 4. Return the registered user
        UserEntity userEntity = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getAccount, userVO.getAccount())
        );
        if (userEntity == null) {
            throw new BusinessException("User registration failed!");
        }
        User dbUser = UserMapStruct.INSTANCE.userEntityToUser(userEntity);
        return RestResult.success(UserMapStruct.INSTANCE.userToUserVO(dbUser));
    }

    @GetMapping("/logout")
    public RestResult<Void> logout() {
        log.warn("用户登出");
        return RestResult.success(null);
    }

    private UserVO warp(UserEntity entity) {
        User user = UserMapStruct.INSTANCE.userEntityToUser(entity);
        return UserMapStruct.INSTANCE.userToUserVO(user);
    }

}
