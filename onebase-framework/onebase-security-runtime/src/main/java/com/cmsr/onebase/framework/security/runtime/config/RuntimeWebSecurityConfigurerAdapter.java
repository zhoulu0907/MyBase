package com.cmsr.onebase.framework.security.runtime.config;

import com.cmsr.onebase.framework.security.runtime.filter.RemoteCallAuthenticationFilter;
import com.cmsr.onebase.framework.security.runtime.filter.RuntimeAuthenticationFilter;
import com.cmsr.onebase.framework.web.config.WebProperties;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * 自定义的 Spring Security 配置适配器实现
 *
 */
@AutoConfiguration
@AutoConfigureOrder(-1) // 目的：先于 Spring Security 自动配置，避免一键改包后，org.* 基础包无法生效
@EnableMethodSecurity(securedEnabled = true)
public class RuntimeWebSecurityConfigurerAdapter {

    /**
     * 认证失败处理类 Bean
     */
    @Resource
    private AuthenticationEntryPoint runtimeAuthenticationEntryPoint;
    /**
     * 权限不够处理器 Bean
     */
    @Resource
    private AccessDeniedHandler runtimeAccessDeniedHandler;
    /**
     * Token 认证过滤器 Bean
     */
    @Resource
    private RuntimeAuthenticationFilter runtimeAuthenticationTokenFilter;

    @Resource
    private RemoteCallAuthenticationFilter remoteCallAuthenticationFilter;

    @Resource
    private WebProperties webProperties;

    /**
     * 由于 Spring Security 创建 AuthenticationManager 对象时，没声明 @Bean 注解，导致无法被注入
     * 通过覆写父类的该方法，添加 @Bean 注解，解决该问题
     */
    @Bean
    public AuthenticationManager runtimeAuthenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 配置 URL 的安全配置
     *
     */
    @Bean
    protected SecurityFilterChain runtimeFilterChain(HttpSecurity httpSecurity) throws Exception {
        // 设置运行时API的安全匹配器
        RequestMatcher runtimeApiMatcher = new OrRequestMatcher(
            new AntPathRequestMatcher(webProperties.getRuntimeApi().getPrefix() + "/**")
        );
        httpSecurity.securityMatcher(runtimeApiMatcher);
        
        // 登出
        httpSecurity
                // 开启跨域
                .cors(Customizer.withDefaults())
                // CSRF 禁用，因为不使用 Session
                .csrf(AbstractHttpConfigurer::disable)
                // 基于 token 机制，所以不需要 Session
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                // 一堆自定义的 Spring Security 处理器
                .exceptionHandling(c -> c.authenticationEntryPoint(runtimeAuthenticationEntryPoint)
                        .accessDeniedHandler(runtimeAccessDeniedHandler));

        // 添加 Token Filter
        httpSecurity.addFilterBefore(runtimeAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        //httpSecurity.addFilterBefore(remoteCallAuthenticationFilter, RuntimeAuthenticationFilter.class);
        return httpSecurity.build();
    }

}