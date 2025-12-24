package com.cmsr.onebase.plugin.simulator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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
                        .anyRequest().permitAll());

        return http.build();
    }

    /**
     * 配置内存用户（仅用于测试环境，避免自动生成密码警告）
     * <p>
     * 注意：由于所有请求都配置为 permitAll()，此用户实际上不会被使用。
     * 配置此 Bean 的目的是防止 Spring Security 自动生成随机密码并输出警告日志。
     * </p>
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin")
                        .password("{noop}admin") // {noop} 表示不加密
                        .roles("ADMIN")
                        .build());
    }

}
