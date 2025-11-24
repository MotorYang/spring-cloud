package com.yangxy.cloud.system.main.user.service.impl;

import com.yangxy.cloud.system.main.user.dto.User;
import com.yangxy.cloud.system.main.user.entity.UserEntity;
import com.yangxy.cloud.system.main.user.mapper.UserMapStruct;
import com.yangxy.cloud.system.main.user.mapper.UserMapper;
import com.yangxy.cloud.system.main.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/24 17:23
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<User> getAllUser() {
        List<UserEntity> userEntities = userMapper.selectList(null);
        return userEntities.stream().map(UserMapStruct.INSTANCE::userEntityToUser).toList();
    }
}
