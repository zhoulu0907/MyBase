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
 * DEV 模式测试 - auto-load=true, auto-start=false
 * <p>
 * 测试场景：插件自动加载但不自动启动
 * </p>
 * <p>
 * 预期行为：
 * <ul>
 * <li>插件自动加载到 RESOLVED 状态</li>
 * <li>插件未启动，API 不可访问（返回 404）</li>
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
                "onebase.plugin.auto-load=true",
                "onebase.plugin.auto-start=false",
                "onebase.plugin.dev-class-paths[0]=../onebase-plugin-demo/plugin-demo-hello/target/classes"
})
public class DevModeAutoLoadTrueAutoStartFalseTest {

        private static final Logger log = LoggerFactory.getLogger(DevModeAutoLoadTrueAutoStartFalseTest.class);
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
                        // 确保测试开始前插件处于正确状态（已加载但未启动）
                        // 如果已启动，则停止
                        if (pluginManager.getPluginState(DEV_PLUGIN_ID) == PluginState.STARTED) {
                                pluginManager.stopPlugin(DEV_PLUGIN_ID);
                        }
                        // 如果未加载，则加载
                        if (pluginManager.getPlugin(DEV_PLUGIN_ID) == null) {
                                pluginManager.loadPlugins();
                        }
                }
        }

        @Test
        @DisplayName("验证插件已加载但未启动")
        void testPluginLoadedButNotStarted() {
                log.info("=== 测试：插件已加载但未启动 ===");

                if (pluginManager == null) {
                        log.warn("插件管理器未创建，跳过测试");
                        return;
                }

                // 验证插件存在
                PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                                .exists();

                // 验证插件状态为 RESOLVED（已加载但未启动）
                PluginState state = pluginManager.getPluginState(DEV_PLUGIN_ID);
                assertThat(state)
                                .as("插件应该已加载但未启动")
                                .isIn(PluginState.RESOLVED, PluginState.CREATED);

                // 验证插件不在已启动列表中
                PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                                .isNotInStartedList();

                log.info("✓ 插件已加载但未启动，状态: {}", state);
        }

        @Test
        @DisplayName("验证未启动时 API 不可访问")
        void testApiNotAccessibleWhenNotStarted() {
                log.info("=== 测试：未启动时 API 不可访问 ===");

                if (pluginManager == null) {
                        log.warn("插件管理器未创建，跳过测试");
                        return;
                }

                // 验证插件未启动
                PluginState state = pluginManager.getPluginState(DEV_PLUGIN_ID);
                assertThat(state).isNotEqualTo(PluginState.STARTED);

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
                                        .as("API %s 应该不可访问（插件未启动）", apiPath)
                                        .isNotEqualTo(200);
                        log.debug("  ✓ API {} 返回 {} (不可访问)", apiPath, response.getStatusCode());
                }

                log.info("✓ 所有 API 在插件未启动时不可访问");
        }

        @Test
        @DisplayName("手动启动后 API 变为可访问")
        void testApiAccessibleAfterManualStart() {
                log.info("=== 测试：手动启动后 API 变为可访问 ===");

                if (pluginManager == null) {
                        log.warn("插件管理器未创建，跳过测试");
                        return;
                }

                // 1. 验证初始状态：插件未启动
                PluginState initialState = pluginManager.getPluginState(DEV_PLUGIN_ID);
                assertThat(initialState).isNotEqualTo(PluginState.STARTED);
                log.info("初始状态: {}", initialState);

                // 2. 验证 API 不可访问
                HttpResponse beforeStart = httpUtil.get("/plugin/hello-plugin/hello");
                assertThat(beforeStart.getStatusCode())
                                .as("启动前 API 应该不可访问")
                                .isNotEqualTo(200);
                log.info("启动前 API 状态: {}", beforeStart.getStatusCode());

                // 3. 手动启动插件
                log.info("手动启动插件...");
                PluginState startedState = pluginManager.startPlugin(DEV_PLUGIN_ID);
                assertThat(startedState).isEqualTo(PluginState.STARTED);
                log.info("插件已启动，状态: {}", startedState);

                // 4. 验证插件状态
                PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                                .isStarted()
                                .isInStartedList();

                // 5. 验证 API 现在可访问
                HttpResponse afterStart = httpUtil.get("/plugin/hello-plugin/hello");
                afterStart.assertSuccess()
                                .assertJsonFieldExists("message")
                                .assertJsonFieldExists("timestamp")
                                .assertJsonFieldExists("plugin");

                String message = afterStart.getJsonField("message");
                assertThat(message).contains("hello");
                log.info("启动后 API 响应: {}", message);

                log.info("✓ 手动启动后 API 成功变为可访问");
        }

        @Test
        @DisplayName("完整生命周期：加载(未启动) -> 启动 -> 停止")
        void testFullLifecycle() {
                log.info("=== 测试：完整生命周期 ===");

                if (pluginManager == null) {
                        log.warn("插件管理器未创建，跳过测试");
                        return;
                }

                // 阶段 1: 验证初始状态（已加载但未启动）
                log.info("阶段 1: 验证初始状态");
                PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                                .exists()
                                .isNotStarted()
                                .isNotInStartedList();
                HttpResponse response1 = httpUtil.get("/plugin/hello-plugin/hello");
                assertThat(response1.getStatusCode()).isNotEqualTo(200);
                log.info("  ✓ 插件已加载但未启动，API 不可访问");

                // 阶段 2: 启动插件
                log.info("阶段 2: 启动插件");
                pluginManager.startPlugin(DEV_PLUGIN_ID);
                PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                                .isStarted()
                                .isInStartedList();
                HttpResponse response2 = httpUtil.get("/plugin/hello-plugin/hello");
                response2.assertSuccess();
                log.info("  ✓ 插件已启动，API 可访问");

                // 阶段 3: 停止插件
                log.info("阶段 3: 停止插件");
                pluginManager.stopPlugin(DEV_PLUGIN_ID);
                PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                                .isStopped()
                                .isNotInStartedList();
                HttpResponse response3 = httpUtil.get("/plugin/hello-plugin/hello");
                assertThat(response3.getStatusCode()).isNotEqualTo(200);
                log.info("  ✓ 插件已停止，API 不可访问");

                log.info("✓ 完整生命周期测试通过");
        }
}
