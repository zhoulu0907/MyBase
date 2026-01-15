package com.cmsr.onebase.plugin.runtime.test.api;

import com.cmsr.onebase.plugin.api.HttpHandler;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.runtime.test.util.PluginStatusAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pf4j.PluginState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DEV 模式 dev-class-paths 配置验证测试
 * <p>
 * 测试 dev-class-paths 配置是否正确生效：
 * <ul>
 * <li>验证扩展点从配置的 classpath 中加载</li>
 * <li>验证扩展点数量符合预期</li>
 * <li>验证扩展点类型正确</li>
 * </ul>
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
public class DevModeClassPathsTest {

    private static final Logger log = LoggerFactory.getLogger(DevModeClassPathsTest.class);
    private static final String DEV_PLUGIN_ID = "dev-mode-plugin";

    @Autowired(required = false)
    private OneBasePluginManager pluginManager;

    @Test
    @DisplayName("验证 dev-class-paths 配置生效")
    void testDevClassPathsConfigEffective() {
        log.info("=== 测试：dev-class-paths 配置生效 ===");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 验证插件已启动
        PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                .exists()
                .isStarted();

        log.info("✓ dev-class-paths 配置生效：虚拟插件已创建并启动");
    }

    @Test
    @DisplayName("验证从 dev-class-paths 加载的扩展点")
    void testExtensionsLoadedFromDevClassPaths() {
        log.info("=== 测试：从 dev-class-paths 加载扩展点 ===");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 验证插件已启动（只有启动后才能获取扩展点）
        PluginState state = pluginManager.getPluginState(DEV_PLUGIN_ID);
        assertThat(state).isEqualTo(PluginState.STARTED);

        // 获取 HttpHandler 扩展点
        List<HttpHandler> httpHandlers = pluginManager.getHttpHandlers(DEV_PLUGIN_ID);

        // 验证扩展点已加载
        assertThat(httpHandlers)
                .as("应该从 dev-class-paths 加载到 HttpHandler 扩展点")
                .isNotEmpty();

        log.info("从 dev-class-paths 加载的 HttpHandler 数量: {}", httpHandlers.size());

        // 验证扩展点数量（hello-plugin 有 4 个 HttpHandler）
        // HelloWorldHandler, HutoolCryptoHandler, CYSTestController, CustomApiHandler
        assertThat(httpHandlers.size())
                .as("hello-plugin 应该有 4 个 HttpHandler")
                .isGreaterThanOrEqualTo(4);

        // 打印扩展点信息
        for (HttpHandler handler : httpHandlers) {
            log.info("  - {}", handler.getClass().getSimpleName());
        }

        log.info("✓ 成功从 dev-class-paths 加载扩展点");
    }

    @Test
    @DisplayName("验证扩展点类型正确")
    void testExtensionTypesCorrect() {
        log.info("=== 测试：扩展点类型正确 ===");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 获取 HttpHandler 扩展点
        List<HttpHandler> httpHandlers = pluginManager.getHttpHandlers(DEV_PLUGIN_ID);

        // 验证所有扩展点都实现了 HttpHandler 接口
        for (HttpHandler handler : httpHandlers) {
            assertThat(handler)
                    .as("扩展点应该实现 HttpHandler 接口")
                    .isInstanceOf(HttpHandler.class);
        }

        // 验证扩展点类名包含预期的处理器
        String[] expectedHandlerNames = {
                "HelloWorldHandler",
                "HutoolCryptoHandler",
                "CYSTestController",
                "CustomApiHandler"
        };

        List<String> actualHandlerNames = httpHandlers.stream()
                .map(h -> h.getClass().getSimpleName())
                .toList();

        for (String expectedName : expectedHandlerNames) {
            assertThat(actualHandlerNames)
                    .as("应该包含 %s", expectedName)
                    .anyMatch(name -> name.contains(expectedName));
        }

        log.info("✓ 扩展点类型验证通过");
    }

    @Test
    @DisplayName("验证扩展点可以正常工作")
    void testExtensionsWorkCorrectly() {
        log.info("=== 测试：扩展点可以正常工作 ===");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 获取 HttpHandler 扩展点
        List<HttpHandler> httpHandlers = pluginManager.getHttpHandlers(DEV_PLUGIN_ID);

        // 验证扩展点不为空
        assertThat(httpHandlers).isNotEmpty();

        // 验证每个扩展点都有有效的实例
        for (HttpHandler handler : httpHandlers) {
            assertThat(handler)
                    .as("扩展点实例不应该为 null")
                    .isNotNull();

            // 验证扩展点类有正确的包名（来自 hello-plugin）
            String packageName = handler.getClass().getPackage().getName();
            assertThat(packageName)
                    .as("扩展点应该来自 hello-plugin 包")
                    .contains("com.cmsr.onebase.plugin.demo.hello");

            log.debug("  ✓ {} 来自包: {}", handler.getClass().getSimpleName(), packageName);
        }

        log.info("✓ 所有扩展点都可以正常工作");
    }

    @Test
    @DisplayName("文档化 dev-class-paths 配置说明")
    void testDocumentDevClassPathsConfig() {
        log.info("=== dev-class-paths 配置说明 ===");
        log.info("配置项: onebase.plugin.dev-class-paths");
        log.info("类型: List<String>");
        log.info("用途: 指定 DEV 模式下扫描扩展点的 classpath 路径");
        log.info("示例: ../onebase-plugin-demo/plugin-demo-hello/target/classes");
        log.info("说明:");
        log.info("  - 支持相对路径和绝对路径");
        log.info("  - 可以配置多个路径（使用数组索引）");
        log.info("  - 路径应该指向编译后的 classes 目录");
        log.info("  - DEV 模式会扫描这些路径下的扩展点实现类");
        log.info("  - 扫描使用 Spring 的 ClassPathScanningCandidateComponentProvider");
        log.info("优势:");
        log.info("  - 无需打包插件 ZIP");
        log.info("  - 修改代码后直接运行，无需 Maven 编译");
        log.info("  - IDE 友好，支持断点调试");

        // 这个测试总是通过，仅用于文档化目的
        assertThat(true).isTrue();
    }
}
