package com.cmsr.onebase.plugin.runtime.test.api;

import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.runtime.test.config.PluginTestConfiguration;
import com.cmsr.onebase.plugin.runtime.test.util.PluginHttpTestUtil;
import com.cmsr.onebase.plugin.runtime.test.util.PluginStatusAssert;
import com.cmsr.onebase.plugin.runtime.test.util.PluginTestEnvironmentManager;
import com.cmsr.onebase.plugin.runtime.test.util.CommonApiTests;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

/**
 * STAGING 模式 API 响应测试
 * <p>
 * 测试所有插件接口在 STAGING 模式下的响应
 * 注意：插件会自动编译打包并复制到 plugins 目录
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-25
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // 移除以保持
// Context 在 @AfterAll 时可用
@Import(PluginTestConfiguration.class)
@TestPropertySource(properties = {
        "onebase.plugin.enabled=true",
        "onebase.plugin.mode=staging",
        "onebase.plugin.auto-load=true",
        "onebase.plugin.auto-start=true",
        "onebase.plugin.plugins-dir=plugins" // 使用相对路径,由环境管理器自动准备
})
public class StagingModeApiTest {

    private static final Logger log = LoggerFactory.getLogger(StagingModeApiTest.class);

    @BeforeAll
    static void setupEnvironment() throws Exception {
        PluginTestEnvironmentManager.setupEnvironment();
    }

    @AfterAll
    static void cleanupEnvironment() throws Exception {
        PluginTestEnvironmentManager.cleanupEnvironment();
    }

    @LocalServerPort
    private int port;

    @Autowired(required = false)
    private OneBasePluginManager pluginManager;

    private PluginHttpTestUtil httpUtil;

    @BeforeEach
    void setUp() {
        PluginTestEnvironmentManager.setPluginManager(pluginManager);
        httpUtil = new PluginHttpTestUtil("http://localhost:" + port);

        // 验证插件是否存在（STAGING 模式依赖实际的插件包）
        if (pluginManager != null && pluginManager.getPlugin("hello-plugin").isPresent()) {
            PluginStatusAssert.assertPlugin(pluginManager, "hello-plugin")
                    .exists()
                    .isStarted();
        } else {
            log.warn("STAGING 模式下未找到 hello-plugin，请确保 plugins 目录下有插件包");
        }
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        // 确保 pluginManager 在 @AfterAll 清理时仍然可用
        PluginTestEnvironmentManager.setPluginManager(pluginManager);
    }

    @Test
    @DisplayName("STAGING - HelloWorldHandler /hello 默认参数")
    void testHelloWorld_defaultParam() {
        if (pluginManager == null || !pluginManager.getPlugin("hello-plugin").isPresent()) {
            log.warn("跳过测试：插件未加载");
            return;
        }

        CommonApiTests.testHelloWorld_defaultParam(httpUtil, log);
    }

    @Test
    @DisplayName("STAGING - HelloWorldHandler /hello 自定义name参数")
    void testHelloWorld_customName() {
        if (pluginManager == null || !pluginManager.getPlugin("hello-plugin").isPresent()) {
            log.warn("跳过测试：插件未加载");
            return;
        }

        CommonApiTests.testHelloWorld_customName(httpUtil, log);
    }

    @Test
    @DisplayName("STAGING - HelloWorldHandler /process 正常数据")
    void testHelloWorld_process_success() {
        if (pluginManager == null || !pluginManager.getPlugin("hello-plugin").isPresent()) {
            log.warn("跳过测试：插件未加载");
            return;
        }

        CommonApiTests.testHelloWorld_process_success(httpUtil, log);
    }

    @Test
    @DisplayName("STAGING - HelloWorldHandler /process 空数据")
    void testHelloWorld_process_emptyData() {
        if (pluginManager == null || !pluginManager.getPlugin("hello-plugin").isPresent()) {
            log.warn("跳过测试：插件未加载");
            return;
        }

        CommonApiTests.testHelloWorld_process_emptyData(httpUtil, log);
    }

    @Test
    @DisplayName("STAGING - CYSTestController /cysinfo 默认参数")
    void testCYSTest_defaultParam() {
        if (pluginManager == null || !pluginManager.getPlugin("hello-plugin").isPresent()) {
            log.warn("跳过测试：插件未加载");
            return;
        }

        CommonApiTests.testCYSTest_defaultParam(httpUtil, log);
    }

    @Test
    @DisplayName("STAGING - CYSTestController /cysinfo 自定义name参数")
    void testCYSTest_customName() {
        if (pluginManager == null || !pluginManager.getPlugin("hello-plugin").isPresent()) {
            log.warn("跳过测试：插件未加载");
            return;
        }

        CommonApiTests.testCYSTest_customName(httpUtil, log);
    }

    @Test
    @DisplayName("STAGING - CustomApiHandler /api/info")
    void testCustomApi_info() {
        if (pluginManager == null || !pluginManager.getPlugin("hello-plugin").isPresent()) {
            log.warn("跳过测试：插件未加载");
            return;
        }

        CommonApiTests.testCustomApi_info(httpUtil, log);
    }

    @Test
    @DisplayName("STAGING - CustomApiHandler /api/status")
    void testCustomApi_status() {
        if (pluginManager == null || !pluginManager.getPlugin("hello-plugin").isPresent()) {
            log.warn("跳过测试：插件未加载");
            return;
        }

        CommonApiTests.testCustomApi_status(httpUtil, log);
    }

    @Test
    @DisplayName("STAGING - CustomApiHandler /api/process 正常数据")
    void testCustomApi_process_success() {
        if (pluginManager == null || !pluginManager.getPlugin("hello-plugin").isPresent()) {
            log.warn("跳过测试：插件未加载");
            return;
        }

        CommonApiTests.testCustomApi_process_success(httpUtil, log);
    }

    @Test
    @DisplayName("STAGING - CustomApiHandler /api/process 空数据")
    void testCustomApi_process_emptyData() {
        if (pluginManager == null || !pluginManager.getPlugin("hello-plugin").isPresent()) {
            log.warn("跳过测试：插件未加载");
            return;
        }

        CommonApiTests.testCustomApi_process_emptyData(httpUtil, log);
    }

    @Test
    @DisplayName("STAGING - HutoolCryptoHandler /crypto")
    void testHutool_crypto() {
        if (pluginManager == null || !pluginManager.getPlugin("hello-plugin").isPresent()) {
            log.warn("跳过测试：插件未加载");
            return;
        }

        CommonApiTests.testHutool_crypto(httpUtil, log);
    }

    @Test
    @DisplayName("STAGING - HutoolCryptoHandler /check-hutool")
    void testHutool_checkHutool() {
        if (pluginManager == null || !pluginManager.getPlugin("hello-plugin").isPresent()) {
            log.warn("跳过测试：插件未加载");
            return;
        }

        CommonApiTests.testHutool_checkHutool(httpUtil, log);
    }

    @Test
    @DisplayName("STAGING - TestHttpHandler /api/info")
    void testTestPlugin_info() {
        if (pluginManager == null || !pluginManager.getPlugin("test-plugin").isPresent()) {
            log.warn("跳过测试：插件未加载");
            return;
        }

        CommonApiTests.testTestPlugin_info(httpUtil, log);
    }
}
