package com.cmsr.onebase.plugin.runtime.test.integration;

import com.cmsr.onebase.plugin.api.HttpHandler;
import com.cmsr.onebase.plugin.runtime.http.PluginHttpManager;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 插件系统集成冒烟测试
 * <p>
 * 验证插件系统的完整集成和核心功能，包括：
 * <ul>
 * <li>Spring Boot 自动配置正确加载</li>
 * <li>插件管理器正确初始化</li>
 * <li>扩展点自动发现和注册</li>
 * <li>HTTP 路由动态注册</li>
 * <li>Spring Bean 集成</li>
 * </ul>
 * </p>
 * <p>
 * 这是一个快速的冒烟测试，用于验证系统的基本健康状态。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-25
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(properties = {
        "onebase.plugin.enabled=true",
        "onebase.plugin.mode=dev",
        "onebase.plugin.auto-load=true",
        "onebase.plugin.auto-start=true",
        "onebase.plugin.dev-class-paths[0]=../onebase-plugin-demo/plugin-demo-hello/target/classes"
})
public class PluginSystemIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(PluginSystemIntegrationTest.class);

    @Autowired(required = false)
    private OneBasePluginManager pluginManager;

    @Autowired(required = false)
    private PluginHttpManager httpManager;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Test
    @DisplayName("插件系统集成冒烟测试")
    void testPluginSystemIntegration() {
        log.info("========== 插件系统集成验证开始 ==========");

        // ========== 第一部分：Spring Boot 自动配置验证 ==========
        log.info("\n【第一部分】Spring Boot 自动配置验证");

        assertThat(applicationContext).as("ApplicationContext 应该被创建").isNotNull();
        log.info("✅ 1.1 Spring ApplicationContext 创建成功");

        assertThat(pluginManager).as("OneBasePluginManager 应该被自动配置").isNotNull();
        log.info("✅ 1.2 OneBasePluginManager 自动配置成功");

        assertThat(httpManager).as("PluginHttpManager 应该被自动配置").isNotNull();
        log.info("✅ 1.3 PluginHttpManager 自动配置成功");

        assertThat(requestMappingHandlerMapping).as("RequestMappingHandlerMapping 应该存在").isNotNull();
        log.info("✅ 1.4 Spring MVC RequestMappingHandlerMapping 集成正常");

        // ========== 第二部分：插件加载和启动验证 ==========
        log.info("\n【第二部分】插件加载和启动验证");

        List<?> plugins = pluginManager.getPlugins();
        assertThat(plugins).as("应该加载插件").isNotEmpty();
        log.info("✅ 2.1 已加载插件数量: {}", plugins.size());

        List<?> startedPlugins = pluginManager.getStartedPlugins();
        assertThat(startedPlugins).as("应该有已启动的插件").isNotEmpty();
        log.info("✅ 2.2 已启动插件数量: {}", startedPlugins.size());

        boolean hasDevPlugin = pluginManager.getPlugin("dev-mode-plugin").isPresent();
        assertThat(hasDevPlugin).as("DEV 模式下应该有 dev-mode-plugin").isTrue();
        log.info("✅ 2.3 dev-mode-plugin 加载成功");

        // ========== 第三部分：扩展点发现和注册验证 ==========
        log.info("\n【第三部分】扩展点发现和注册验证");

        var httpHandlers = pluginManager.getHttpHandlers("dev-mode-plugin");
        assertThat(httpHandlers).as("应该发现 HttpHandler 扩展点").isNotEmpty();
        log.info("✅ 3.1 发现 {} 个 HttpHandler 扩展点", httpHandlers.size());

        httpHandlers.forEach(handler -> {
            log.info("   - {}", handler.getClass().getSimpleName());
        });

        // 验证 HttpHandler 被注册为 Spring Bean
        boolean allRegisteredAsBeans = httpHandlers.stream()
                .allMatch(handler -> {
                    String[] beanNames = applicationContext.getBeanNamesForType(handler.getClass());
                    return beanNames.length > 0;
                });
        assertThat(allRegisteredAsBeans).as("所有 HttpHandler 应该被注册为 Spring Bean").isTrue();
        log.info("✅ 3.2 所有 HttpHandler 已注册为 Spring Bean");

        // ========== 第四部分：HTTP 路由注册验证 ==========
        log.info("\n【第四部分】HTTP 路由注册验证");

        List<Object> pluginHandlers = httpManager.getPluginHandlers("dev-mode-plugin");
        assertThat(pluginHandlers).as("PluginHttpManager 应该能查询到插件的 HttpHandler").isNotEmpty();
        log.info("✅ 4.1 PluginHttpManager 查询到 {} 个 HttpHandler", pluginHandlers.size());

        // 验证路由已注册到 Spring MVC
        int handlerMethodCount = requestMappingHandlerMapping.getHandlerMethods().size();
        assertThat(handlerMethodCount).as("应该有注册的 Handler Method").isGreaterThan(0);
        log.info("✅ 4.2 Spring MVC 已注册 {} 个 Handler Method", handlerMethodCount);

        // ========== 验证总结 ==========
        log.info("\n========== 集成验证总结 ==========");
        log.info("✅ Spring Boot 自动配置：正常");
        log.info("✅ 插件加载和启动：正常");
        log.info("✅ 扩展点发现和注册：正常");
        log.info("✅ HTTP 路由动态注册：正常");
        log.info("✅ Spring Bean 集成：正常");
        log.info("\n【结论】插件系统集成验证通过，系统运行正常");
        log.info("========== 集成验证完成 ==========");
    }

    @Test
    @DisplayName("插件系统配置验证")
    void testPluginConfiguration() {
        log.info("验证插件系统配置...");

        // 验证关键 Bean 存在
        assertThat(applicationContext.containsBean("oneBasePluginManager"))
                .as("oneBasePluginManager Bean 应该存在").isTrue();

        assertThat(applicationContext.containsBean("pluginHttpManager"))
                .as("pluginHttpManager Bean 应该存在").isTrue();

        assertThat(applicationContext.containsBean("pluginControllerRegistrar"))
                .as("pluginControllerRegistrar Bean 应该存在").isTrue();

        log.info("✅ 所有关键 Bean 配置正确");
    }
}
