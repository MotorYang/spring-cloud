package com.yangxy.cloud.system.main.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/24 17:30
 */
@Configuration
@MapperScan("com.yangxy.cloud.system.main.user.dao")
public class UserConfig {

}
