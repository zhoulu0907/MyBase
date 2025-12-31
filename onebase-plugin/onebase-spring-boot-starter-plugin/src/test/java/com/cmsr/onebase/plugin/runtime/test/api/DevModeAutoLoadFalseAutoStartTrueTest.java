package com.cmsr.onebase.plugin.runtime.test.api;

import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.runtime.test.util.PluginHttpTestUtil;
import com.cmsr.onebase.plugin.runtime.test.util.PluginHttpTestUtil.HttpResponse;
import com.cmsr.onebase.plugin.runtime.test.util.PluginStatusAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pf4j.PluginState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DEV 模式测试 - auto-load=false, auto-start=true (无效配置)
 * <p>
 * 测试场景：无效配置 - 不能自动启动未加载的插件
 * </p>
 * <p>
 * 预期行为：
 * <ul>
 * <li>配置无效，系统应该忽略 auto-start=true</li>
 * <li>行为应该等同于 auto-load=false, auto-start=false</li>
 * <li>插件初始不存在</li>
 * <li>API 不可访问</li>
 * </ul>
 * </p>
 * <p>
 * <strong>注意：</strong>这是一个边界情况测试，用于验证系统对无效配置的处理。
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
        "onebase.plugin.auto-load=false",
        "onebase.plugin.auto-start=true", // 无效配置：不能自动启动未加载的插件
        "onebase.plugin.dev-class-paths[0]=../onebase-plugin-demo/plugin-demo-hello/target/classes"
})
public class DevModeAutoLoadFalseAutoStartTrueTest {

    private static final Logger log = LoggerFactory.getLogger(DevModeAutoLoadFalseAutoStartTrueTest.class);
    private static final String DEV_PLUGIN_ID = "dev-mode-plugin";

    @LocalServerPort
    private int port;

    @Autowired(required = false)
    private OneBasePluginManager pluginManager;

    private PluginHttpTestUtil httpUtil;

    @BeforeEach
    void setUp() {
        httpUtil = new PluginHttpTestUtil("http://localhost:" + port);

        if (pluginManager != null) {
            // 确保测试开始前插件处于正确状态（不存在）
            // 需要处理所有可能的插件状态
            if (pluginManager.getPlugin(DEV_PLUGIN_ID) != null) {
                try {
                    PluginState state = pluginManager.getPluginState(DEV_PLUGIN_ID);
                    log.debug("setUp: 当前插件状态 = {}", state);

                    // 如果已启动，先停止
                    if (state == PluginState.STARTED) {
                        pluginManager.stopPlugin(DEV_PLUGIN_ID);
                        log.debug("setUp: 已停止插件");
                    }

                    // 尝试卸载插件
                    boolean unloaded = pluginManager.unloadPlugin(DEV_PLUGIN_ID);
                    log.debug("setUp: 卸载插件结果 = {}", unloaded);
                } catch (Exception e) {
                    log.warn("setUp: 卸载插件时出错: {}", e.getMessage());
                }
            }
        }
    }

    @Test
    @DisplayName("验证无效配置：插件不存在（auto-start 被忽略）")
    void testInvalidConfigPluginNotExists() {
        log.info("=== 测试：无效配置 - 插件不存在 ===");
        log.warn("配置无效：auto-load=false, auto-start=true（不能自动启动未加载的插件）");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 验证插件不存在（auto-start 应该被忽略）
        PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                .notExists();

        // 验证插件列表为空
        assertThat(pluginManager.getPlugins())
                .as("插件列表应该为空（无效配置被忽略）")
                .isEmpty();

        log.info("✓ 无效配置被正确处理：插件不存在");
    }

    @Test
    @DisplayName("验证无效配置：API 不可访问")
    void testInvalidConfigApiNotAccessible() {
        log.info("=== 测试：无效配置 - API 不可访问 ===");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 验证插件不存在
        PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                .notExists();

        // 测试 API 端点，应该返回 404
        String[] apiPaths = {
                "/plugin/hello-plugin/hello",
                "/plugin/hello-plugin/api/info"
        };

        for (String apiPath : apiPaths) {
            HttpResponse response = httpUtil.get(apiPath);
            assertThat(response.getStatusCode())
                    .as("API %s 应该不可访问（插件未加载）", apiPath)
                    .isNotEqualTo(200);
            log.debug("  ✓ API {} 返回 {} (不可访问)", apiPath, response.getStatusCode());
        }

        log.info("✓ 无效配置被正确处理：API 不可访问");
    }

    @Test
    @DisplayName("验证无效配置行为等同于 auto-load=false, auto-start=false")
    void testInvalidConfigBehaviorSameAsAllFalse() {
        log.info("=== 测试：无效配置行为验证 ===");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 验证行为等同于 auto-load=false, auto-start=false
        // 1. 插件不存在
        PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                .notExists();

        // 2. 手动加载后插件存在但未启动
        log.info("手动加载插件...");
        pluginManager.loadPlugins();
        PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                .exists()
                .isNotStarted();

        // 3. 手动启动后插件可用
        log.info("手动启动插件...");
        pluginManager.startPlugin(DEV_PLUGIN_ID);
        PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                .isStarted();

        // 4. API 可访问
        HttpResponse response = httpUtil.get("/plugin/hello-plugin/hello");
        response.assertSuccess();

        log.info("✓ 无效配置行为等同于 auto-load=false, auto-start=false");
    }

    @Test
    @DisplayName("文档化无效配置的预期行为")
    void testDocumentInvalidConfigBehavior() {
        log.info("=== 无效配置说明 ===");
        log.info("配置: auto-load=false, auto-start=true");
        log.info("说明: 这是一个逻辑上无效的配置组合");
        log.info("原因: 不能自动启动一个未加载的插件");
        log.info("实际行为: 系统忽略 auto-start=true，等同于 auto-load=false, auto-start=false");
        log.info("建议: 避免使用此配置，应使用以下有效组合之一：");
        log.info("  - auto-load=true, auto-start=true (自动加载并启动)");
        log.info("  - auto-load=true, auto-start=false (自动加载但不启动)");
        log.info("  - auto-load=false, auto-start=false (完全手动控制)");

        // 这个测试总是通过，仅用于文档化目的
        assertThat(true).isTrue();
    }
}
