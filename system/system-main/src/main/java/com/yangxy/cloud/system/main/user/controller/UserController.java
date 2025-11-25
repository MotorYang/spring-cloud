package com.yangxy.cloud.system.main.user.controller;

import com.yangxy.cloud.common.response.RestResult;
import com.yangxy.cloud.system.main.user.dto.User;
import com.yangxy.cloud.system.main.user.mapper.UserMapStruct;
import com.yangxy.cloud.system.main.user.service.UserService;
import com.yangxy.cloud.system.main.user.vo.UserVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/24 17:22
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/all")
    RestResult<List<UserVO>> getAllUser() {
        List<User> users = userService.getAllUser();
        return RestResult.success(users.stream().map(UserMapStruct.INSTANCE::userToUserVO).toList());
    }

    @GetMapping("/create")
    void createTestUser() {
        userService.createTestData();
    }

}
