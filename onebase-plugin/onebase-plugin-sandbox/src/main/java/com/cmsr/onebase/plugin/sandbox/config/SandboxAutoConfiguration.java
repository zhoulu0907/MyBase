package com.cmsr.onebase.plugin.sandbox.config;

import com.cmsr.onebase.plugin.sandbox.executor.PluginSandboxExecutor;
import com.cmsr.onebase.plugin.sandbox.manager.PluginSecurityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 插件沙箱自动配置
 * <p>
 * 自动配置插件沙箱相关的 Bean
 * </p>
 *
 * @author OneBase Plugin Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(SandboxProperties.class)
@ConditionalOnProperty(prefix = "onebase.plugin.sandbox", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SandboxAutoConfiguration {

    /**
     * 配置安全管理器
     */
    @Bean
    public PluginSecurityManager pluginSecurityManager() {
        log.info("初始化插件安全管理器");
        PluginSecurityManager securityManager = new PluginSecurityManager();

        // 设置为默认安全管理器
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(securityManager);
            log.info("已设置插件安全管理器为系统默认安全管理器");
        }

        return securityManager;
    }

    /**
     * 配置沙箱执行器
     */
    @Bean
    public PluginSandboxExecutor pluginSandboxExecutor(
            PluginSecurityManager pluginSecurityManager,
            SandboxProperties sandboxProperties) {
        log.info("初始化插件沙箱执行器，最大线程数: {}", sandboxProperties.getMaxThreads());
        return new PluginSandboxExecutor(
                pluginSecurityManager,
                sandboxProperties.getMaxThreads()
        );
    }
}
