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
 * DEV 模式测试 - auto-load=false, auto-start=false
 * <p>
 * 测试场景：插件不自动加载也不自动启动
 * </p>
 * <p>
 * 预期行为：
 * <ul>
 * <li>插件初始不存在</li>
 * <li>API 不可访问（返回 404）</li>
 * <li>手动加载后，插件进入 RESOLVED 状态，API 仍不可访问</li>
 * <li>手动启动后，API 变为可访问</li>
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
                "onebase.plugin.auto-load=false",
                "onebase.plugin.auto-start=false",
                "onebase.plugin.dev-class-paths[0]=../onebase-plugin-demo/plugin-demo-hello/target/classes"
})
public class DevModeAutoLoadFalseAutoStartFalseTest {

        private static final Logger log = LoggerFactory.getLogger(DevModeAutoLoadFalseAutoStartFalseTest.class);
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
        @DisplayName("验证插件初始不存在")
        void testPluginNotExistsInitially() {
                log.info("=== 测试：插件初始不存在 ===");

                if (pluginManager == null) {
                        log.warn("插件管理器未创建，跳过测试");
                        return;
                }

                // 验证插件不存在
                PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                                .notExists();

                // 验证插件列表为空
                assertThat(pluginManager.getPlugins())
                                .as("插件列表应该为空")
                                .isEmpty();

                log.info("✓ 插件初始不存在");
        }

        @Test
        @DisplayName("验证初始状态下 API 不可访问")
        void testApiNotAccessibleInitially() {
                log.info("=== 测试：初始状态下 API 不可访问 ===");

                if (pluginManager == null) {
                        log.warn("插件管理器未创建，跳过测试");
                        return;
                }

                // 验证插件不存在
                PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                                .notExists();

                // 测试多个 API 端点，都应该返回 404
                String[] apiPaths = {
                                "/plugin/hello-plugin/hello",
                                "/plugin/hello-plugin/process",
                                "/plugin/hello-plugin/check-hutool",
                                "/plugin/hello-plugin/cysinfo",
                                "/plugin/hello-plugin/api/info"
                };

                for (String apiPath : apiPaths) {
                        HttpResponse response = httpUtil.get(apiPath);
                        assertThat(response.getStatusCode())
                                        .as("API %s 应该不可访问（插件未加载）", apiPath)
                                        .isNotEqualTo(200);
                        log.debug("  ✓ API {} 返回 {} (不可访问)", apiPath, response.getStatusCode());
                }

                log.info("✓ 所有 API 在插件未加载时不可访问");
        }

        @Test
        @DisplayName("手动加载后插件存在但未启动")
        void testPluginExistsAfterManualLoad() {
                log.info("=== 测试：手动加载后插件存在但未启动 ===");

                if (pluginManager == null) {
                        log.warn("插件管理器未创建，跳过测试");
                        return;
                }

                // 1. 验证初始状态：插件不存在
                PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                                .notExists();
                log.info("初始状态: 插件不存在");

                // 2. 手动加载插件
                log.info("手动加载插件...");
                pluginManager.loadPlugins();

                // 3. 验证插件已加载
                PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                                .exists();

                // 4. 验证插件状态为 RESOLVED（已加载但未启动）
                PluginState state = pluginManager.getPluginState(DEV_PLUGIN_ID);
                assertThat(state)
                                .as("插件应该已加载但未启动")
                                .isIn(PluginState.RESOLVED, PluginState.CREATED);
                log.info("加载后状态: {}", state);

                // 5. 验证插件不在已启动列表中
                PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                                .isNotInStartedList();

                // 6. 验证 API 仍不可访问（已加载但未启动）
                HttpResponse response = httpUtil.get("/plugin/hello-plugin/hello");
                assertThat(response.getStatusCode())
                                .as("加载后但未启动时 API 应该不可访问")
                                .isNotEqualTo(200);
                log.info("加载后 API 状态: {} (仍不可访问)", response.getStatusCode());

                log.info("✓ 手动加载后插件存在但未启动，API 不可访问");
        }

        @Test
        @DisplayName("完整流程：手动加载 -> 手动启动 -> API 可访问")
        void testFullManualFlow() {
                log.info("=== 测试：完整手动流程 ===");

                if (pluginManager == null) {
                        log.warn("插件管理器未创建，跳过测试");
                        return;
                }

                // 阶段 1: 验证初始状态（插件不存在）
                log.info("阶段 1: 验证初始状态");
                PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                                .notExists();
                HttpResponse response1 = httpUtil.get("/plugin/hello-plugin/hello");
                assertThat(response1.getStatusCode()).isNotEqualTo(200);
                log.info("  ✓ 插件不存在，API 不可访问");

                // 阶段 2: 手动加载插件
                log.info("阶段 2: 手动加载插件");
                pluginManager.loadPlugins();
                PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                                .exists()
                                .isNotStarted()
                                .isNotInStartedList();
                HttpResponse response2 = httpUtil.get("/plugin/hello-plugin/hello");
                assertThat(response2.getStatusCode()).isNotEqualTo(200);
                log.info("  ✓ 插件已加载但未启动，API 仍不可访问");

                // 阶段 3: 手动启动插件
                log.info("阶段 3: 手动启动插件");
                PluginState startedState = pluginManager.startPlugin(DEV_PLUGIN_ID);
                assertThat(startedState).isEqualTo(PluginState.STARTED);
                PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                                .isStarted()
                                .isInStartedList();
                HttpResponse response3 = httpUtil.get("/plugin/hello-plugin/hello");
                response3.assertSuccess()
                                .assertJsonFieldExists("message")
                                .assertJsonFieldExists("timestamp");
                log.info("  ✓ 插件已启动，API 可访问");

                // 阶段 4: 验证 API 功能正常
                log.info("阶段 4: 验证 API 功能");
                String message = response3.getJsonField("message");
                assertThat(message).contains("hello");
                log.info("  ✓ API 响应正常: {}", message);

                log.info("✓ 完整手动流程测试通过");
        }

        @Test
        @DisplayName("完整生命周期：加载 -> 启动 -> 停止 -> 卸载")
        void testFullLifecycle() {
                log.info("=== 测试：完整生命周期 ===");

                if (pluginManager == null) {
                        log.warn("插件管理器未创建，跳过测试");
                        return;
                }

                // 阶段 1: 加载插件
                log.info("阶段 1: 加载插件");
                pluginManager.loadPlugins();
                PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                                .exists()
                                .isNotStarted();

                // 阶段 2: 启动插件
                log.info("阶段 2: 启动插件");
                pluginManager.startPlugin(DEV_PLUGIN_ID);
                PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                                .isStarted();
                httpUtil.get("/plugin/hello-plugin/hello").assertSuccess();

                // 阶段 3: 停止插件
                log.info("阶段 3: 停止插件");
                pluginManager.stopPlugin(DEV_PLUGIN_ID);
                PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                                .isStopped();
                HttpResponse response = httpUtil.get("/plugin/hello-plugin/hello");
                assertThat(response.getStatusCode()).isNotEqualTo(200);

                // 阶段 4: 卸载插件
                log.info("阶段 4: 卸载插件");
                boolean unloaded = pluginManager.unloadPlugin(DEV_PLUGIN_ID);
                assertThat(unloaded).isTrue();
                PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                                .notExists();

                log.info("✓ 完整生命周期测试通过");
        }
}
