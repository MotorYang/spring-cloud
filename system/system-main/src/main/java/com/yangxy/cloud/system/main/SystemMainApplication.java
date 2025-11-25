package com.yangxy.cloud.system.main;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/24 07:36
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.yangxy.cloud")
public class SystemMainApplication implements CommandLineRunner {

    @Resource
    private DataSource dataSource;
    @Value("${spring.application.name}")
    private String applicationName;

    private static final Logger log = LoggerFactory.getLogger(SystemMainApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SystemMainApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("{} 启动成功！", applicationName);
        // 检查数据库连接
        try (Connection connection = dataSource.getConnection()) {
            log.info("[✔] 数据库连接成功！连接信息: URL={}, 用户名={}", connection.getMetaData().getURL(), connection.getMetaData().getUserName());
        } catch (SQLException e) {
            log.error("[x] 数据库连接失败！App退出，错误信息: {}", e.getMessage());
            System.exit(0);
        }
    }
}
