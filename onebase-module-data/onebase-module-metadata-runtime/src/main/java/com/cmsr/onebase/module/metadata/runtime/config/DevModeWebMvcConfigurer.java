package com.cmsr.onebase.module.metadata.runtime.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册 DevMode 拦截器
 *
 * <p>仅拦截 /runtime/metadata/** 路径下的请求，
 * 覆盖 SemanticDynamicDataController、DraftSemanticDynamicDataController
 * 和 SemanticAttachmentController 三个控制器。</p>
 *
 * @author bty418
 * @date 2026-02-12
 */
@Configuration
public class DevModeWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DevModeHandlerInterceptor())
                .addPathPatterns("/runtime/metadata/**");
    }
}
