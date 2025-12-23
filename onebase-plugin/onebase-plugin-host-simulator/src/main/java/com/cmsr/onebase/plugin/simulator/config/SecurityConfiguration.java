package com.cmsr.onebase.plugin.simulator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 插件模拟器的 Security 配置
 * <p>
 * 允许所有插件相关接口匿名访问，方便测试和演示。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-17
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF（测试环境）
                .csrf(csrf -> csrf.disable())
                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        // Swagger接口文档
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/swagger-ui").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        // Spring Boot Actuator
                        .requestMatchers("/actuator/**").permitAll()
                        // 插件管理接口（允许匿名访问）
                        .requestMatchers("/api/plugin/**").permitAll()
                        // 插件HTTP路由接口（允许匿名访问）
                        .requestMatchers("/plugin/**").permitAll()
                        // 其他所有请求也允许访问（测试环境）
                        .anyRequest().permitAll()
                );

        return http.build();
    }

}
