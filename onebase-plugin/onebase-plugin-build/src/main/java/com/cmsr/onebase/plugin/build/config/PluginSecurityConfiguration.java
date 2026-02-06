package com.cmsr.onebase.plugin.build.config;

import com.cmsr.onebase.framework.web.core.util.StaticResourceUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class PluginSecurityConfiguration {

    @Bean
    public WebSecurityCustomizer pluginWebSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(new AntPathRequestMatcher(StaticResourceUtil.PLUGIN_STATIC_PATTERN));
    }

    /**
     * 配置静态资源的安全过滤器链
     * <p>
     * 显式配置过滤器链并允许匿名访问，优先级设置为最高，
     * 确保在其他通用过滤器链之前生效。
     * </p>
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain pluginStaticResourcesFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(StaticResourceUtil.PLUGIN_STATIC_PATTERN)
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
