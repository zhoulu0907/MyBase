package com.cmsr.onebase.plugin.runtime.config;

import com.cmsr.onebase.plugin.runtime.interceptor.PluginSecurityInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 插件Web配置
 * <p>
 * 注册插件相关的拦截器、过滤器等。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-13
 */
@Configuration
public class PluginWebConfig implements WebMvcConfigurer {

    @Resource
    private PluginSecurityInterceptor pluginSecurityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册插件安全拦截器，拦截所有 /plugin/** 路由
        registry.addInterceptor(pluginSecurityInterceptor)
                .addPathPatterns("/plugin/**")
                .order(50);
    }
}
