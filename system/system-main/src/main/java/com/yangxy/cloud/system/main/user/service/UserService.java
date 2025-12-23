package com.yangxy.cloud.system.main.user.service;

import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yangxy.cloud.system.main.user.dao.UserDAO;
import com.yangxy.cloud.system.main.user.entity.User;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserService {

    @Resource
    private UserDAO userDAO;

    /**
     * 根据账户获取用户
     * @param username 账户
     * @return 用户信息
     */
    public User getUser(String username) {
        if (username == null || StringUtils.isBlank(username)){
            return null;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userDAO.selectOne(queryWrapper);
    }

}
