package com.yangxy.system.main.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/24 07:39
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Value("${test.value}")
    private String nacosValue;

    @GetMapping("/nacos_value")
    public String test() {
        return "Nacos测试值：" + nacosValue;
    }

}
