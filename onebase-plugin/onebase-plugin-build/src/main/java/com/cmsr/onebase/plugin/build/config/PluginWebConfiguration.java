package com.cmsr.onebase.plugin.build.config;

import com.cmsr.onebase.plugin.runtime.config.PluginProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * 插件Web配置
 * <p>
 * 配置静态资源映射，将前端插件目录映射到Web访问路径。
 * 保证开发/管理端也能访问到插件的前端资源。
 * </p>
 *
 * @author onebase
 * @date 2026-02-05
 */
@Configuration("pluginBuildWebConfiguration")
@Slf4j
public class PluginWebConfiguration implements WebMvcConfigurer {

    @Resource
    private PluginProperties pluginProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 构建本地文件路径 (file:/absolute/path/to/plugins/frontend/)
        String frontendPath = Paths.get(pluginProperties.getPluginsDir(), pluginProperties.getFrontendDir())
                .toAbsolutePath().toUri().toString();
        
        String contextPath = pluginProperties.getFrontendContextPath();
        if (!contextPath.endsWith("/")) {
            contextPath += "/";
        }
        
        // 映射路径: /plugins/static/** -> file:/absolute/path/to/plugins/frontend/
        // 这样前端可以通过 /plugins/static/{pluginId}/{version}/index.html 访问
        registry.addResourceHandler(contextPath + "**")
                .addResourceLocations(frontendPath);
                
        log.info("管理端插件静态资源映射已配置: {} -> {}", contextPath + "**", frontendPath);
    }
}
