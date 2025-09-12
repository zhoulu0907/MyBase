package com.cmsr.onebase.module.metadata.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 为 metadata-runtime 模块下的所有控制器统一添加 URL 前缀，例如：/runtime。
 * 仅对包 com.cmsr.onebase.module.metadata.controller 及其子包生效，避免影响其他模块。
 *
 * 可通过配置项 onebase.web.runtime-path-prefix 自定义前缀，默认 /runtime。
 *
 * @author matianyu
 * @date 2025-09-12
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "onebase.web", name = "runtime-path-prefix-enabled", havingValue = "true", matchIfMissing = true)
public class RuntimeApiPathPrefixConfig implements WebMvcConfigurer {

    /**
     * 运行时接口统一前缀，默认 /runtime
     */
    @Value("${onebase.web.runtime-path-prefix:/runtime}")
    private String runtimePathPrefix;

    /**
     * 仅给 metadata-runtime 模块的 controller 包添加统一前缀
     *
     * @param configurer Spring MVC 路径匹配配置
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        String prefix = (runtimePathPrefix == null) ? "" : runtimePathPrefix.trim();
        if (!StringUtils.hasText(prefix)) {
            return;
        }
        // 仅匹配本模块 controller 包，避免影响其他模块
        configurer.addPathPrefix(prefix,
                HandlerTypePredicate.forBasePackage("com.cmsr.onebase.module.metadata.controller"));
    }
}
