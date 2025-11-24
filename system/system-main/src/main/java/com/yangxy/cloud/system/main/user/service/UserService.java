package com.yangxy.cloud.system.main.user.service;

import com.yangxy.cloud.system.main.user.dto.User;

import java.util.List;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/24 17:23
 */
public interface UserService {

    /**
     * 获取所有用户
     *
     * @return 用户列表
     */
    List<User> getAllUser();

}
