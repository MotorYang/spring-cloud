package com.yangxy.cloud.system.main.user.controller;

import com.yangxy.cloud.common.exception.BusinessException;
import com.yangxy.cloud.common.response.RestResult;
import com.yangxy.cloud.security.common.context.UserContext;
import com.yangxy.cloud.system.main.user.dto.UserDTO;
import com.yangxy.cloud.system.main.user.entity.User;
import com.yangxy.cloud.system.main.user.mapper.UserMapper;
import com.yangxy.cloud.system.main.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/info")
    RestResult<UserDTO> info() {
        String username = UserContext.getCurrentUsername();
        if (username == null) {
            throw new BusinessException(11003, "Unauthorized");
        }
        User user = userService.getUser(username);
        if (user == null) {
            throw new BusinessException(11003, "Unauthorized");
        }
        return RestResult.success(UserMapper.INSTANCE.toDTO(user));
    }

}
