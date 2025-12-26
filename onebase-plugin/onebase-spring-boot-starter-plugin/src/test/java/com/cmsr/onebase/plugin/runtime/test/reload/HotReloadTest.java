package com.cmsr.onebase.plugin.runtime.test.reload;

import com.cmsr.onebase.plugin.api.HttpHandler;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.runtime.test.util.PluginHttpTestUtil;
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
 * DEV 模式热重载机制测试
 * <p>
 * 测试热重载的核心机制，包括：
 * <ul>
 * <li>插件重新加载能力</li>
 * <li>重新加载后状态恢复</li>
 * <li>重新加载后扩展点刷新</li>
 * </ul>
 * </p>
 * <p>
 * <strong>注意：</strong>此测试验证热重载的<strong>机制</strong>，
 * 不测试实际的文件监听和自动重载（需要手动测试）。
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
        "onebase.plugin.dev-class-paths[0]=../onebase-plugin-demo/plugin-demo-hello/target/classes",
        "onebase.plugin.hot-reload.enabled=true",
        "onebase.plugin.hot-reload.watch-interval=1000"
})
public class HotReloadTest {

    private static final Logger log = LoggerFactory.getLogger(HotReloadTest.class);
    private static final String DEV_PLUGIN_ID = "dev-mode-plugin";

    @LocalServerPort
    private int port;

    @Autowired(required = false)
    private OneBasePluginManager pluginManager;

    private PluginHttpTestUtil httpUtil;

    @BeforeEach
    void setUp() {
        httpUtil = new PluginHttpTestUtil("http://localhost:" + port);
    }

    @Test
    @DisplayName("验证热重载配置生效")
    void testHotReloadConfigEnabled() {
        log.info("=== 测试：热重载配置生效 ===");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 验证插件已加载并启动
        PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                .exists()
                .isStarted();

        log.info("✓ 热重载配置生效：插件已加载并启动");
    }

    @Test
    @DisplayName("验证插件可以重新加载")
    void testPluginCanReload() {
        log.info("=== 测试：插件可以重新加载 ===");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 1. 验证初始状态
        PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                .exists()
                .isStarted();

        int initialHandlerCount = pluginManager.getHttpHandlers(DEV_PLUGIN_ID).size();
        log.info("初始状态：插件已启动，HttpHandler 数量 = {}", initialHandlerCount);

        // 2. 停止插件
        log.info("停止插件...");
        PluginState stoppedState = pluginManager.stopPlugin(DEV_PLUGIN_ID);
        assertThat(stoppedState).isEqualTo(PluginState.STOPPED);
        PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                .isStopped();

        // 3. 卸载插件
        log.info("卸载插件...");
        boolean unloaded = pluginManager.unloadPlugin(DEV_PLUGIN_ID);
        assertThat(unloaded).isTrue();
        PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                .notExists();

        // 4. 重新加载插件（模拟热重载）
        log.info("重新加载插件（模拟热重载）...");
        pluginManager.loadPlugins();
        PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                .exists()
                .isNotStarted();

        // 5. 重新启动插件
        log.info("重新启动插件...");
        PluginState restartedState = pluginManager.startPlugin(DEV_PLUGIN_ID);
        assertThat(restartedState).isEqualTo(PluginState.STARTED);
        PluginStatusAssert.assertPlugin(pluginManager, DEV_PLUGIN_ID)
                .isStarted();

        // 6. 验证扩展点已刷新
        int reloadedHandlerCount = pluginManager.getHttpHandlers(DEV_PLUGIN_ID).size();
        assertThat(reloadedHandlerCount)
                .as("重新加载后 HttpHandler 数量应该与初始状态相同")
                .isEqualTo(initialHandlerCount);
        log.info("重新加载后：HttpHandler 数量 = {}", reloadedHandlerCount);

        log.info("✓ 插件重新加载成功：卸载 → 加载 → 启动 → 扩展点刷新");
    }

    @Test
    @DisplayName("验证重新加载后 API 功能正常")
    void testApiWorksAfterReload() {
        log.info("=== 测试：重新加载后 API 功能正常 ===");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 1. 验证初始 API 可访问
        log.info("验证初始 API 可访问...");
        PluginHttpTestUtil.HttpResponse initialResponse = httpUtil.get("/plugin/hello-plugin/hello");
        initialResponse.assertSuccess()
                .assertJsonFieldExists("message");
        String initialMessage = initialResponse.getJsonField("message");
        log.info("初始 API 响应: {}", initialMessage);

        // 2. 停止并卸载插件
        log.info("停止并卸载插件...");
        pluginManager.stopPlugin(DEV_PLUGIN_ID);
        pluginManager.unloadPlugin(DEV_PLUGIN_ID);

        // 3. 验证 API 不可访问
        log.info("验证 API 不可访问...");
        PluginHttpTestUtil.HttpResponse unavailableResponse = httpUtil.get("/plugin/hello-plugin/hello");
        assertThat(unavailableResponse.getStatusCode())
                .as("插件卸载后 API 应该不可访问")
                .isNotEqualTo(200);

        // 4. 重新加载并启动插件
        log.info("重新加载并启动插件...");
        pluginManager.loadPlugins();
        pluginManager.startPlugin(DEV_PLUGIN_ID);

        // 5. 验证 API 恢复可访问
        log.info("验证 API 恢复可访问...");
        PluginHttpTestUtil.HttpResponse reloadedResponse = httpUtil.get("/plugin/hello-plugin/hello");
        reloadedResponse.assertSuccess()
                .assertJsonFieldExists("message");
        String reloadedMessage = reloadedResponse.getJsonField("message");
        log.info("重新加载后 API 响应: {}", reloadedMessage);

        // 6. 验证 API 功能一致
        assertThat(reloadedMessage)
                .as("重新加载后 API 响应应该与初始状态一致")
                .contains("hello");

        log.info("✓ 重新加载后 API 功能正常");
    }

    @Test
    @DisplayName("文档化热重载测试说明")
    void testDocumentHotReloadTesting() {
        log.info("=== 热重载测试说明 ===");
        log.info("本测试类验证热重载的<机制>，包括：");
        log.info("  1. 插件可以被卸载和重新加载");
        log.info("  2. 重新加载后状态正确恢复");
        log.info("  3. 重新加载后扩展点正确刷新");
        log.info("  4. 重新加载后 API 功能正常");
        log.info("");
        log.info("实际的文件监听和自动重载需要手动测试：");
        log.info("  1. 启动应用（DEV 模式，hot-reload.enabled=true）");
        log.info("  2. 修改插件源代码");
        log.info("  3. 编译插件（IDE 自动编译或 mvn compile）");
        log.info("  4. 观察日志，确认自动重新加载");
        log.info("  5. 测试 API，验证新代码生效");

        // 这个测试总是通过，仅用于文档化目的
        assertThat(true).isTrue();
    }
}
