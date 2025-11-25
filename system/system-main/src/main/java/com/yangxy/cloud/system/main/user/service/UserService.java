package com.yangxy.cloud.system.main.user.service;

import com.yangxy.cloud.system.main.user.dto.User;

import java.util.List;
import java.util.Optional;

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

    /**
     * 根据账号获取用户
     *
     * @param account 用户账号
     * @return 用户信息
     */
    Optional<User> getUserByAccount(String account);

    /**
     * 创建一些测试用户
     */
    void createTestData();

}
