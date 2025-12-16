package com.yangxy.cloud.gateway.config;

import com.yangxy.cloud.gateway.properties.CorsProperties;
import com.yangxy.cloud.gateway.properties.CustomGatewayProperties;
import com.yangxy.cloud.security.utils.JwtUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/25 06:08
 * Spring Security 配置
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Resource
    private CorsProperties corsProperties;
    @Resource
    private CustomGatewayProperties customGatewayProperties;

    private Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // 配置自定义的 AuthenticationWebFilter
        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(authenticationManager());
        jwtFilter.setServerAuthenticationConverter(new JwtAuthenticationConverter());
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        // 放行登陆接口：注意这里匹配的是gateway转发后的路径
                        .pathMatchers(customGatewayProperties.getWhitelist().toArray(new String[0])).permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 前端地址
        config.setAllowedOrigins(corsProperties.getAllowedOrigins());
        for (String allowedOrigin : corsProperties.getAllowedOrigins()) {
            logger.info("Allowed-Origins: ".concat(allowedOrigin));
        }
        // 允许方法
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 允许的请求头
        config.setAllowedHeaders(List.of("*"));
        // 允许携带 cookie / authorization
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    /**
     * 第一步：认证管理器，验证Token有效性
     *
     * @return 认证对象
     */
    private ReactiveAuthenticationManager authenticationManager() {
        return authentication -> {
            String token = authentication.getCredentials().toString();
            if (JwtUtils.validateToken(token)) {
                String account = JwtUtils.getUserAccount(token);
                // 验证通过，返回认证对象
                return Mono.just(new UsernamePasswordAuthenticationToken(
                        account, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                ));
            }
            return Mono.empty();
        };
    }

    /**
     * 第二步：从Header获取Token
     */
    private static class JwtAuthenticationConverter implements ServerAuthenticationConverter {
        @Override
        public Mono<Authentication> convert(ServerWebExchange exchange) {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                return Mono.just(new UsernamePasswordAuthenticationToken(token, token));
            }
            return Mono.empty();
        }
    }

}
