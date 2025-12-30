package com.cmsr.onebase.plugin.runtime.config;

import com.cmsr.onebase.plugin.runtime.interceptor.PluginSecurityInterceptor;
import org.springframework.beans.factory.ObjectProvider;
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

    private final ObjectProvider<PluginSecurityInterceptor> interceptorProvider;

    public PluginWebConfig(ObjectProvider<PluginSecurityInterceptor> interceptorProvider) {
        this.interceptorProvider = interceptorProvider;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册插件安全拦截器,拦截所有 /plugin/** 路由
        // 使用 ObjectProvider 延迟获取,避免循环依赖
        interceptorProvider.ifAvailable(interceptor -> registry.addInterceptor(interceptor)
                .addPathPatterns("/plugin/**")
                .order(50));
    }
}
