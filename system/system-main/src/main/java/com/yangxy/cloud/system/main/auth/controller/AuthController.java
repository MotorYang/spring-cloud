package com.yangxy.cloud.system.main.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yangxy.cloud.common.exception.ServiceException;
import com.yangxy.cloud.common.response.RestResult;
import com.yangxy.cloud.security.utils.JwtUtils;
import com.yangxy.cloud.security.vo.LoginRequest;
import com.yangxy.cloud.system.main.user.dto.User;
import com.yangxy.cloud.system.main.user.entity.UserEntity;
import com.yangxy.cloud.system.main.user.mapper.UserMapStruct;
import com.yangxy.cloud.system.main.user.mapper.UserMapper;
import com.yangxy.cloud.system.main.user.vo.UserVO;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/25 06:41
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private UserMapper userMapper;
    @Resource
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest loginRequest) {
        // 1. Query user
        UserEntity user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getAccount, loginRequest.getAccount()));
        if (user == null) {
            throw new ServiceException("User does not exist!");
        }
        // 2. Validate password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ServiceException("Incorrect password");
        }
        // 3. Generate token
        String token = JwtUtils.generateToken(user.getAccount(), user.getId());
        return Map.of("token", token);
    }

    @PostMapping("/register")
    public RestResult<UserVO> register(@RequestBody UserVO userVO) {
        // 1. Validate
        if (userVO.getAccount().isBlank()) {
            throw new ServiceException("Account cannot be empty!");
        }
        if (userVO.getPassword().isBlank()) {
            throw new ServiceException("Password cannot be empty!");
        }
        // 2. Check if user exists
        UserEntity exitsUser = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getAccount, userVO.getAccount())
        );
        if (exitsUser != null) {
            throw new ServiceException("User already exists!");
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
            throw new ServiceException("User registration failed!");
        }
        User dbUser = UserMapStruct.INSTANCE.userEntityToUser(userEntity);
        return RestResult.success(UserMapStruct.INSTANCE.userToUserVO(dbUser));
    }

}
