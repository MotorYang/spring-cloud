package com.yangxy.cloud.security.web.config;

import com.yangxy.cloud.security.web.interceptor.GatewayProtectFilter;
import com.yangxy.cloud.security.web.interceptor.UserContextInterceptor;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(name = "org.springframework.web.servlet.config.annotation.WebMvcConfigurer")
public class WebSecurityAutoConfig implements WebMvcConfigurer {

    @Bean
    public UserContextInterceptor userContextInterceptor() {
        return new UserContextInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userContextInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/system/auth/**");
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
