package com.cmsr.onebase.server.flink.config;

import com.cmsr.onebase.server.flink.filter.RemoteCallAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Author：huangjie
 * @Date：2025/11/14 11:13
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(corsCustomizer -> corsCustomizer.disable())
                .csrf(csrfCustomizer -> csrfCustomizer.disable());
        httpSecurity
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                );
        httpSecurity.httpBasic(httpBasicCustomizer -> httpBasicCustomizer.disable());
        httpSecurity.formLogin(formLoginCustomizer -> formLoginCustomizer.disable());
        // 添加 Token Filter
        RemoteCallAuthenticationFilter remoteCallAuthenticationFilter = new RemoteCallAuthenticationFilter();
        httpSecurity.addFilterBefore(remoteCallAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
