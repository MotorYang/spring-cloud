package com.yangxy.cloud.security.config;

import com.yangxy.cloud.security.filter.GatewayProtectFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/25 07:59
 */
@Configuration
@EnableWebSecurity
// ⭐⭐⭐ 核心重点：加上这句话，Gateway 就会自动忽略这个类，不会报错 ⭐⭐⭐
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SystemSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // 既然 Gateway 已经把关，内部服务可以信任流量，或者做简单的 IP 白名单
        // 这里配置为允许所有请求，并禁用 CSRF
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 注册网关保护过滤器
     */
    @Bean
    public FilterRegistrationBean<GatewayProtectFilter> gatewayProtectFilter() {
        FilterRegistrationBean<GatewayProtectFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new GatewayProtectFilter());
        // 拦截所有路径
        registration.addUrlPatterns("/*");
        // 设置名称
        registration.setName("GatewayProtectFilter");
        // 设置优先级：必须要在 Spring Security 之前执行，或者尽量靠前
        // Ordered.HIGHEST_PRECEDENCE 确保它在最前面被执行
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

}
