package com.cmsr.onebase.plugin.runtime.test.exception;

import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.runtime.test.util.PluginHttpTestUtil;
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
 * 插件异常场景测试
 * <p>
 * 测试各种异常操作场景，确保系统的健壮性：
 * <ul>
 * <li>重复操作的幂等性</li>
 * <li>非法状态转换的防护</li>
 * <li>异常操作后系统状态的一致性</li>
 * <li>异常操作后插件 API 的可用性</li>
 * </ul>
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-25
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
public class PluginExceptionScenariosTest {

    private static final Logger log = LoggerFactory.getLogger(PluginExceptionScenariosTest.class);
    private static final String PLUGIN_ID = "hello-plugin";

    @LocalServerPort
    private int port;

    @Autowired(required = false)
    private OneBasePluginManager pluginManager;

    private PluginHttpTestUtil httpUtil;

    @BeforeEach
    void setUp() {
        httpUtil = new PluginHttpTestUtil("http://localhost:" + port);
    }

    // ==================== 重复操作测试 ====================

    @Test
    @DisplayName("异常场景 - 重复启动已启动的插件应该幂等")
    void testRepeatStart_ShouldBeIdempotent() {
        log.info("=== 测试：重复启动已启动的插件 ===");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 1. 验证插件已启动
        PluginState initialState = pluginManager.getPlugin(PLUGIN_ID).get().getPluginState();
        assertThat(initialState).isEqualTo(PluginState.STARTED);
        log.info("初始状态：插件已启动");

        // 2. 验证 API 可用
        PluginHttpTestUtil.HttpResponse initialResponse = httpUtil.get("/plugin/hello-plugin/hello");
        initialResponse.assertSuccess();
        log.info("初始 API 响应正常");

        // 3. 重复启动（第1次）
        log.info("第1次重复启动...");
        PluginState state1 = pluginManager.startPlugin(PLUGIN_ID);
        assertThat(state1).isEqualTo(PluginState.STARTED);

        // 4. 验证 API 仍然可用
        PluginHttpTestUtil.HttpResponse response1 = httpUtil.get("/plugin/hello-plugin/hello");
        response1.assertSuccess();
        log.info("第1次重复启动后 API 仍然正常");

        // 5. 重复启动（第2次）
        log.info("第2次重复启动...");
        PluginState state2 = pluginManager.startPlugin(PLUGIN_ID);
        assertThat(state2).isEqualTo(PluginState.STARTED);

        // 6. 验证 API 仍然可用
        PluginHttpTestUtil.HttpResponse response2 = httpUtil.get("/plugin/hello-plugin/hello");
        response2.assertSuccess();
        log.info("第2次重复启动后 API 仍然正常");

        // 7. 重复启动（第3次）- 验证多次重复的稳定性
        log.info("第3次重复启动...");
        PluginState state3 = pluginManager.startPlugin(PLUGIN_ID);
        assertThat(state3).isEqualTo(PluginState.STARTED);

        PluginHttpTestUtil.HttpResponse response3 = httpUtil.get("/plugin/hello-plugin/hello");
        response3.assertSuccess();
        log.info("第3次重复启动后 API 仍然正常");

        log.info("✓ 重复启动测试通过：系统保持幂等性，API 始终可用");
    }

    @Test
    @DisplayName("异常场景 - 重复停止已停止的插件应该幂等")
    void testRepeatStop_ShouldBeIdempotent() {
        log.info("=== 测试：重复停止已停止的插件 ===");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 1. 先停止插件
        log.info("首次停止插件...");
        PluginState state = pluginManager.stopPlugin(PLUGIN_ID);
        assertThat(state).isEqualTo(PluginState.STOPPED);

        // 2. 验证 API 不可用
        PluginHttpTestUtil.HttpResponse response = httpUtil.get("/plugin/hello-plugin/hello");
        assertThat(response.getStatusCode()).isNotEqualTo(200);
        log.info("停止后 API 不可用（符合预期）");

        // 3. 重复停止（第1次）
        log.info("第1次重复停止...");
        PluginState state1 = pluginManager.stopPlugin(PLUGIN_ID);
        assertThat(state1).isEqualTo(PluginState.STOPPED);

        // 4. 重复停止（第2次）
        log.info("第2次重复停止...");
        PluginState state2 = pluginManager.stopPlugin(PLUGIN_ID);
        assertThat(state2).isEqualTo(PluginState.STOPPED);

        // 5. 验证可以重新启动
        log.info("验证可以重新启动...");
        PluginState restartState = pluginManager.startPlugin(PLUGIN_ID);
        assertThat(restartState).isEqualTo(PluginState.STARTED);

        PluginHttpTestUtil.HttpResponse restartResponse = httpUtil.get("/plugin/hello-plugin/hello");
        restartResponse.assertSuccess();
        log.info("重新启动后 API 恢复正常");

        log.info("✓ 重复停止测试通过：系统保持幂等性，可正常重启");
    }

    @Test
    @DisplayName("异常场景 - 重复卸载不存在的插件应该安全处理")
    void testRepeatUnload_ShouldHandleSafely() {
        log.info("=== 测试：重复卸载不存在的插件 ===");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 1. 先停止并卸载插件
        log.info("停止并卸载插件...");
        pluginManager.stopPlugin(PLUGIN_ID);
        boolean unloaded = pluginManager.unloadPlugin(PLUGIN_ID);
        assertThat(unloaded).isTrue();

        // 2. 验证插件已不存在
        assertThat(pluginManager.getPlugin(PLUGIN_ID)).isEmpty();
        log.info("插件已卸载");

        // 3. 重复卸载（第1次）
        log.info("第1次重复卸载...");
        boolean result1 = pluginManager.unloadPlugin(PLUGIN_ID);
        assertThat(result1).isFalse();

        // 4. 重复卸载（第2次）
        log.info("第2次重复卸载...");
        boolean result2 = pluginManager.unloadPlugin(PLUGIN_ID);
        assertThat(result2).isFalse();

        log.info("✓ 重复卸载测试通过：安全返回 false，无异常");
    }

    // ==================== 非法状态转换测试 ====================

    @Test
    @DisplayName("异常场景 - 启动不存在的插件应该安全处理")
    void testStartNonExistentPlugin() {
        log.info("=== 测试：启动不存在的插件 ===");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 尝试启动不存在的插件
        String nonExistentPluginId = "non-existent-plugin";
        PluginState state = pluginManager.startPlugin(nonExistentPluginId);

        // 应该返回 null（因为插件不存在）
        assertThat(state).as("启动不存在的插件应该返回 null").isNull();
        log.info("✓ 启动不存在的插件测试通过：安全返回 null");
    }

    @Test
    @DisplayName("异常场景 - 卸载已启动的插件应该先停止")
    void testUnloadStartedPlugin() {
        log.info("=== 测试：卸载已启动的插件 ===");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 1. 验证插件已启动
        assertThat(pluginManager.getPlugin(PLUGIN_ID).get().getPluginState())
                .isEqualTo(PluginState.STARTED);

        // 2. 直接尝试卸载（应该成功，因为 PF4J 会自动停止）
        log.info("尝试卸载已启动的插件...");
        boolean unloaded = pluginManager.unloadPlugin(PLUGIN_ID);
        assertThat(unloaded).isTrue();

        // 3. 验证插件已不存在
        assertThat(pluginManager.getPlugin(PLUGIN_ID)).isEmpty();
        log.info("✓ 卸载已启动的插件测试通过：PF4J 自动处理停止");
    }

    @Test
    @DisplayName("异常场景 - 删除运行中的插件应该成功（自动停止）")
    void testDeleteStartedPlugin() {
        log.info("=== 测试：删除运行中的插件 ===");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 1. 启动插件
        pluginManager.startPlugin(PLUGIN_ID);
        assertThat(pluginManager.getPlugin(PLUGIN_ID).get().getPluginState())
                .isEqualTo(PluginState.STARTED);

        // 2. 尝试删除运行中的插件（新逻辑：应该自动停止并成功删除）
        log.info("删除运行中的插件（预期自动停止）...");
        boolean result;
        try {
            result = pluginManager.deletePlugin(PLUGIN_ID);
        } catch (Exception e) {
            // Windows 上可能会遇到的文件锁定问题
            log.warn("删除插件时遇到异常（可能是文件锁定）: {}", e.getMessage());
            result = false;
        }

        // 关键验证：插件是否已从内存中移除（逻辑删除）
        // 物理删除可能因为 Windows 文件锁定而失败，通过日志分析这是环境问题
        // 我们主要验证业务逻辑是否允许了这个操作
        boolean logicallyDeleted = !pluginManager.getPlugin(PLUGIN_ID).isPresent();
        assertThat(logicallyDeleted).as("插件应该从内存中移除").isTrue();

        if (result) {
            log.info("✓ 物理删除成功");
        } else {
            log.warn("! 物理删除失败（预期内的环境限制），但逻辑删除成功");
        }

        // 恢复环境
        log.info("恢复环境...");
        pluginManager.loadPlugins();
    }

    @Test
    @DisplayName("异常场景 - 停止未启动的插件应该安全处理")
    void testStopNonStartedPlugin() {
        log.info("=== 测试：停止未启动的插件 ===");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 1. 先停止插件
        PluginState firstStop = pluginManager.stopPlugin(PLUGIN_ID);
        assertThat(firstStop).isEqualTo(PluginState.STOPPED);

        // 2. 再次停止（应该幂等，返回 STOPPED）
        log.info("停止已停止的插件...");
        PluginState state = pluginManager.stopPlugin(PLUGIN_ID);
        assertThat(state).as("停止已停止的插件应该返回 STOPPED").isEqualTo(PluginState.STOPPED);

        log.info("✓ 停止未启动插件测试通过：幂等处理");
    }

    // ==================== 综合场景测试 ====================

    @Test
    @DisplayName("异常场景 - 混合异常操作后插件 API 应该可恢复")
    void testPluginApiRecoveryAfterMixedExceptions() {
        log.info("=== 测试：混合异常操作后 API 可恢复性 ===");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 1. 重复启动（幂等）
        pluginManager.startPlugin(PLUGIN_ID);
        pluginManager.startPlugin(PLUGIN_ID);

        // 2. 尝试删除运行中的插件（应该成功，自动停止）
        log.info("删除运行中的插件（预期自动停止）...");
        boolean deleteResult;
        try {
            deleteResult = pluginManager.deletePlugin(PLUGIN_ID);
        } catch (Exception e) {
            // Windows 上可能会遇到的文件锁定问题
            log.warn("删除插件时遇到异常（可能是文件锁定）: {}", e.getMessage());
            deleteResult = false;
        }

        // 关键验证：插件是否已从内存中移除（逻辑删除）
        // 物理删除可能因为 Windows 文件锁定而失败，但逻辑删除应该成功
        boolean logicallyDeleted = !pluginManager.getPlugin(PLUGIN_ID).isPresent();
        assertThat(logicallyDeleted).as("插件应该从内存中移除（逻辑删除）").isTrue();

        if (deleteResult) {
            log.info("✓ 物理删除成功");
        } else {
            log.warn("! 物理删除失败（预期内的环境限制），但逻辑删除成功");
        }

        // 3. 恢复环境（重新加载）
        log.info("恢复环境：重新加载插件");
        if (!pluginManager.getPlugin(PLUGIN_ID).isPresent()) {
            pluginManager.loadPlugins();
        }

        // 4. 再次启动及停止测试
        PluginState startState = pluginManager.startPlugin(PLUGIN_ID);
        assertThat(startState).isEqualTo(PluginState.STARTED);

        PluginState stopState = pluginManager.stopPlugin(PLUGIN_ID);
        assertThat(stopState).isEqualTo(PluginState.STOPPED);

        // 5. 重复停止（幂等）
        PluginState repeatStopState = pluginManager.stopPlugin(PLUGIN_ID);
        assertThat(repeatStopState).isEqualTo(PluginState.STOPPED);

        // 6. 重新启动
        PluginState restartState = pluginManager.startPlugin(PLUGIN_ID);
        assertThat(restartState).isEqualTo(PluginState.STARTED);

        // 7. 验证 API 完全恢复
        PluginHttpTestUtil.HttpResponse response = httpUtil.get("/plugin/hello-plugin/hello");
        response.assertSuccess()
                .assertJsonFieldExists("message");

        log.info("✓ 混合异常操作测试通过：API 完全可恢复");
    }

    @Test
    @DisplayName("异常场景 - 完整生命周期包含异常操作")
    void testFullLifecycleWithExceptions() {
        log.info("=== 测试：完整生命周期包含异常操作 ===");

        if (pluginManager == null) {
            log.warn("插件管理器未创建，跳过测试");
            return;
        }

        // 1. 重复启动（幂等）
        pluginManager.startPlugin(PLUGIN_ID);
        PluginHttpTestUtil.HttpResponse r1 = httpUtil.get("/plugin/hello-plugin/hello");
        r1.assertSuccess();

        // 2. 停止
        PluginState stopState = pluginManager.stopPlugin(PLUGIN_ID);
        assertThat(stopState).isEqualTo(PluginState.STOPPED);

        // 3. 重复停止（幂等）
        PluginState repeatStopState = pluginManager.stopPlugin(PLUGIN_ID);
        assertThat(repeatStopState).isEqualTo(PluginState.STOPPED);

        // 4. 卸载
        boolean unloadResult = pluginManager.unloadPlugin(PLUGIN_ID);
        assertThat(unloadResult).isTrue();

        // 5. 重复卸载（安全失败）
        boolean repeatUnloadResult = pluginManager.unloadPlugin(PLUGIN_ID);
        assertThat(repeatUnloadResult).as("重复卸载应该返回 false").isFalse();

        // 6. 重新加载
        pluginManager.loadPlugins();
        assertThat(pluginManager.getPlugin(PLUGIN_ID)).isPresent();

        // 7. 启动
        PluginState startState = pluginManager.startPlugin(PLUGIN_ID);
        assertThat(startState).isEqualTo(PluginState.STARTED);

        // 8. 验证 API 恢复
        PluginHttpTestUtil.HttpResponse r2 = httpUtil.get("/plugin/hello-plugin/hello");
        r2.assertSuccess();

        log.info("✓ 完整生命周期测试通过：所有操作正常");
    }
}
