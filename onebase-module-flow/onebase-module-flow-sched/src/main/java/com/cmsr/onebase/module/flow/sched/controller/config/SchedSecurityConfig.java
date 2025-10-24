package com.cmsr.onebase.module.flow.sched.controller.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @Author：huangjie
 * @Date：2025/10/17 15:29
 */
@Configuration
@EnableWebSecurity
public class SchedSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrfCustomizer -> csrfCustomizer.disable())
                .cors(corsCustomizer -> corsCustomizer.disable())
                .formLogin(formLoginConfigurer -> formLoginConfigurer.disable())
                .httpBasic(config -> config.disable());
        http.authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated()
        );

        return http.build();
    }
}
