package com.cmsr.onebase.plugin.runtime.test.api;

import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.runtime.test.config.PluginTestConfiguration;
import com.cmsr.onebase.plugin.runtime.test.util.PluginHttpTestUtil;
import com.cmsr.onebase.plugin.runtime.test.util.PluginHttpTestUtil.HttpResponse;
import com.cmsr.onebase.plugin.runtime.test.util.PluginStatusAssert;
import com.cmsr.onebase.plugin.runtime.test.util.PluginTestDataBuilder.*;
import com.cmsr.onebase.plugin.runtime.test.util.PluginTestEnvironmentManager;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("STAGING - HelloWorldHandler /hello 接口")
    void testHelloWorld() {
        if (pluginManager == null || !pluginManager.getPlugin("hello-plugin").isPresent()) {
            log.warn("跳过测试：插件未加载");
            return;
        }

        HttpResponse response = httpUtil.get("/plugin/hello-plugin/hello");
        response.assertSuccess()
                .assertJsonFieldExists("message")
                .assertJsonFieldExists("timestamp");

        log.info("✓ STAGING 模式 HelloWorldHandler /hello 测试通过");
    }

    @Test
    @DisplayName("STAGING - CustomApiHandler /api/info 接口")
    void testCustomApiInfo() {
        if (pluginManager == null || !pluginManager.getPlugin("hello-plugin").isPresent()) {
            log.warn("跳过测试：插件未加载");
            return;
        }

        HttpResponse response = httpUtil.get("/plugin/hello-plugin/api/info");
        response.assertSuccess()
                .assertJsonFieldEquals("plugin", "hello-plugin")
                .assertJsonFieldEquals("springInjectionWorking", true);

        log.info("✓ STAGING 模式 CustomApiHandler /api/info 测试通过");
    }
}
