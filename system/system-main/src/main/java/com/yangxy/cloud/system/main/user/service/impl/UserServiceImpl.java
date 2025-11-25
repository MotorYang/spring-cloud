package com.yangxy.cloud.system.main.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yangxy.cloud.system.main.user.dto.User;
import com.yangxy.cloud.system.main.user.entity.UserEntity;
import com.yangxy.cloud.system.main.user.mapper.UserMapStruct;
import com.yangxy.cloud.system.main.user.mapper.UserMapper;
import com.yangxy.cloud.system.main.user.service.UserService;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<User> getUserByAccount(String account) {
        UserEntity userEntity = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getAccount, account)
        );
        return Optional.ofNullable(UserMapStruct.INSTANCE.userEntityToUser(userEntity));
    }

    @Transactional
    @Override
    public void createTestData() {
        UserEntity userEntity = new UserEntity();
        userEntity.setAccount("admin");
        userEntity.setEmail("admin@hibean.top");
        userEntity.setPhone("17782844968");
        userEntity.setCity("西安");
        userEntity.setGender("男");
        userEntity.setPassword("745700Yxy@");

        userMapper.insert(userEntity);
    }
}
