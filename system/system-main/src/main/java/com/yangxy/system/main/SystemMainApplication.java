package com.yangxy.system.main;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/24 07:36
 */
@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
public class SystemMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemMainApplication.class, args);
        log.info("system-main 启动成功");
    }
}
