package com.cmsr.onebase.plugin.runtime.test.lifecycle;

import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.runtime.test.util.PluginHttpTestUtil;
import com.cmsr.onebase.plugin.runtime.test.util.PluginStatusAssert;
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

/**
 * DEV 模式插件生命周期测试
 * <p>
 * 测试 DEV 模式下插件的启动和停止功能。
 * </p>
 * <p>
 * <strong>重要说明：</strong>
 * <ul>
 * <li><strong>DEV 模式限制：</strong>只支持启动/停止循环，不支持卸载后重新加载</li>
 * <li><strong>原因：</strong>DEV 模式使用虚拟插件（dev-mode-plugin），从 classpath 加载扩展点</li>
 * <li><strong>完整生命周期：</strong>卸载→加载→启动 仅在 STAGING/PROD 模式下可用</li>
 * </ul>
 * </p>
 * <p>
 * 本测试类验证：
 * <ul>
 * <li>插件停止功能</li>
 * <li>插件重新启动功能</li>
 * <li>停止后 HTTP 接口不可访问</li>
 * <li>启动/停止循环的稳定性</li>
 * </ul>
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-25
 * @see com.cmsr.onebase.plugin.runtime.manager.DevModePluginManager
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "onebase.plugin.enabled=true",
        "onebase.plugin.mode=staging",
        "onebase.plugin.auto-load=true",
        "onebase.plugin.auto-start=true",
        "onebase.plugin.plugins-dir=D:/cmsr/10_cmsr/CodingSpace/plugins-root"
})
public class PluginLifecycleTest {

    private static final Logger log = LoggerFactory.getLogger(PluginLifecycleTest.class);

    @LocalServerPort
    private int port;

    @Autowired(required = false)
    private OneBasePluginManager pluginManager;

    private PluginHttpTestUtil httpUtil;

    @Test
    @DisplayName("STAGING 完整生命周期测试：停止->验证->卸载->验证->加载->验证->启动->验证")
    void testStagingFullLifecycle() {
        log.info("========================================");
        log.info("开始 STAGING 完整生命周期测试");
        log.info("========================================");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        httpUtil = new PluginHttpTestUtil("http://localhost:" + port);
        String pluginId = "hello-plugin";
        String apiPath = "/plugin/hello-plugin/hello";

        // ==========================================
        // 1. 初始状态检查 (Started)
        // ==========================================
        log.info("\n=== 阶段 1: 初始状态检查 ===");
        performStatusTest(pluginId, PluginState.STARTED, "初始状态");
        performInterfaceTest(apiPath, true, "初始状态");
        log.info("✓ 初始状态验证通过：插件已启动，API 可访问\n");

        // ==========================================
        // 2. 停止插件 (Started -> Stopped)
        // ==========================================
        log.info("=== 阶段 2: 停止插件 ===");
        log.info("执行操作：停止插件 [{}]", pluginId);
        pluginManager.stopPlugin(pluginId);
        log.info("操作完成：插件已停止");

        // 2.1 停止后状态测试
        performStatusTest(pluginId, PluginState.STOPPED, "停止后");

        // 2.2 停止后接口响应测试
        performInterfaceTest(apiPath, false, "停止后");
        log.info("✓ 停止阶段验证通过：状态为 STOPPED，API 不可访问\n");

        // ==========================================
        // 3. 卸载插件 (Stopped -> Unloaded)
        // ==========================================
        log.info("=== 阶段 3: 卸载插件 ===");

        // 保存插件路径，用于后续重新加载
        java.nio.file.Path pluginPath = pluginManager.getPlugin(pluginId)
                .orElseThrow(() -> new RuntimeException("无法获取插件路径"))
                .getPluginPath();
        log.info("插件路径: {}", pluginPath);

        log.info("执行操作：卸载插件 [{}]", pluginId);
        boolean unloaded = pluginManager.unloadPlugin(pluginId);
        if (!unloaded) {
            throw new RuntimeException("插件卸载失败");
        }
        log.info("操作完成：插件已卸载");

        // 3.1 卸载后状态测试
        performUnloadedStatusTest(pluginId, "卸载后");

        // 3.2 卸载后接口响应测试
        performInterfaceTest(apiPath, false, "卸载后");
        log.info("✓ 卸载阶段验证通过：插件已移除，API 不可访问\n");

        // ==========================================
        // 4. 加载插件 (Unloaded -> Resolved/Stopped)
        // ==========================================
        log.info("=== 阶段 4: 加载插件 ===");
        log.info("执行操作：重新加载插件 [{}]", pluginPath);
        String reloadedPluginId = pluginManager.loadPlugin(pluginPath);
        log.info("操作完成：插件已加载，插件ID: {}", reloadedPluginId);

        // 4.1 加载后状态测试
        performLoadedStatusTest(pluginId, "加载后");

        // 4.2 加载后接口响应测试（未启动，应不可访问）
        performInterfaceTest(apiPath, false, "加载(未启动)后");
        log.info("✓ 加载阶段验证通过：插件已加载但未启动，API 不可访问\n");

        // ==========================================
        // 5. 启动插件 (Resolved/Stopped -> Started)
        // ==========================================
        log.info("=== 阶段 5: 启动插件 ===");
        log.info("执行操作：启动插件 [{}]", pluginId);
        PluginState startState = pluginManager.startPlugin(pluginId);
        log.info("操作完成：插件已启动，返回状态: {}", startState);

        // 5.1 启动后状态测试
        performStatusTest(pluginId, PluginState.STARTED, "启动后");

        // 5.2 启动后接口响应测试
        performInterfaceTest(apiPath, true, "启动后");
        log.info("✓ 启动阶段验证通过：状态为 STARTED，API 恢复访问\n");

        log.info("========================================");
        log.info("✓✓✓ STAGING 完整生命周期测试全部通过 ✓✓✓");
        log.info("========================================");
    }

    /**
     * 执行插件状态测试
     *
     * @param pluginId      插件ID
     * @param expectedState 预期状态
     * @param phase         测试阶段描述
     */
    private void performStatusTest(String pluginId, PluginState expectedState, String phase) {
        log.info("  → 状态测试 [{}]", phase);
        PluginStatusAssert statusAssert = PluginStatusAssert.assertPlugin(pluginManager, pluginId)
                .exists()
                .hasState(expectedState);

        // 额外验证：检查是否在已启动列表中
        if (expectedState == PluginState.STARTED) {
            statusAssert.isInStartedList();
            log.info("    ✓ 插件状态: {} (在已启动列表中)", expectedState);
        } else {
            statusAssert.isNotInStartedList();
            log.info("    ✓ 插件状态: {} (不在已启动列表中)", expectedState);
        }
    }

    /**
     * 执行插件已加载但未启动的状态测试
     *
     * @param pluginId 插件ID
     * @param phase    测试阶段描述
     */
    private void performLoadedStatusTest(String pluginId, String phase) {
        log.info("  → 状态测试 [{}]", phase);
        PluginState actualState = PluginStatusAssert.assertPlugin(pluginManager, pluginId)
                .exists()
                .isNotStarted()
                .isNotInStartedList()
                .getState();
        log.info("    ✓ 插件状态: {} (已加载但未启动)", actualState);
    }

    /**
     * 执行插件已卸载的状态测试
     *
     * @param pluginId 插件ID
     * @param phase    测试阶段描述
     */
    private void performUnloadedStatusTest(String pluginId, String phase) {
        log.info("  → 状态测试 [{}]", phase);
        PluginStatusAssert.assertPlugin(pluginManager, pluginId)
                .notExists();
        log.info("    ✓ 插件已从管理器中移除");
    }

    /**
     * 执行插件接口响应测试
     *
     * @param apiPath         API路径
     * @param shouldBeSuccess 是否应该成功访问
     * @param phase           测试阶段描述
     */
    private void performInterfaceTest(String apiPath, boolean shouldBeSuccess, String phase) {
        log.info("  → 接口响应测试 [{}]", phase);
        var response = httpUtil.get(apiPath);

        if (shouldBeSuccess) {
            // 预期成功访问
            response.assertSuccess();
            log.info("    ✓ API 响应: {} OK - 接口可访问", response.getStatusCode());
            if (response.getBody() != null) {
                log.debug("    响应内容: {}", response.getBody());
            }
        } else {
            // 预期无法访问（404 或其他错误状态码）
            if (response.getStatusCode() == 200) {
                throw new RuntimeException(phase + " API 仍然返回 200 OK，预期应不可访问");
            }
            log.info("    ✓ API 响应: {} - 接口不可访问（符合预期）", response.getStatusCode());
        }
    }

}
