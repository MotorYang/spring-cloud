package com.yangxy.cloud.system.main.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yangxy.cloud.system.main.user.entity.UserEntity;

import java.util.List;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/24 17:10
 */
public interface UserMapper extends BaseMapper<UserEntity> {

    List<UserEntity> findAllUserByCity(String city);

}
