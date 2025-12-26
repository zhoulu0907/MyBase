package com.cmsr.onebase.plugin.runtime.test.api;

import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.runtime.test.util.PluginHttpTestUtil;
import com.cmsr.onebase.plugin.runtime.test.util.PluginHttpTestUtil.HttpResponse;
import com.cmsr.onebase.plugin.runtime.test.util.PluginStatusAssert;
import com.cmsr.onebase.plugin.runtime.test.util.PluginTestDataBuilder.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DEV 模式 API 响应测试 - auto-load=true, auto-start=true
 * <p>
 * <strong>测试场景：</strong>插件自动加载并自动启动
 * </p>
 * <p>
 * <strong>预期行为：</strong>
 * <ul>
 * <li>插件自动加载并启动到 STARTED 状态</li>
 * <li>所有 API 端点立即可访问</li>
 * <li>扩展点从 dev-class-paths 配置的路径加载</li>
 * </ul>
 * </p>
 * <p>
 * 测试所有插件接口在 DEV 模式下的响应：
 * <ul>
 * <li>HelloWorldHandler (2个接口)</li>
 * <li>HutoolCryptoHandler (2个接口)</li>
 * <li>CYSTestController (1个接口)</li>
 * <li>CustomApiHandler (3个接口)</li>
 * </ul>
 * </p>
 * <p>
 * <strong>相关测试：</strong>
 * <ul>
 * <li>{@link DevModeAutoLoadTrueAutoStartFalseTest} - auto-load=true,
 * auto-start=false</li>
 * <li>{@link DevModeAutoLoadFalseAutoStartFalseTest} - auto-load=false,
 * auto-start=false</li>
 * <li>{@link DevModeAutoLoadFalseAutoStartTrueTest} - auto-load=false,
 * auto-start=true (无效配置)</li>
 * <li>{@link DevModeClassPathsTest} - dev-class-paths 配置验证</li>
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
public class DevModeApiTest {

    private static final Logger log = LoggerFactory.getLogger(DevModeApiTest.class);

    @LocalServerPort
    private int port;

    @Autowired(required = false)
    private OneBasePluginManager pluginManager;

    private PluginHttpTestUtil httpUtil;

    @BeforeEach
    void setUp() {
        httpUtil = new PluginHttpTestUtil("http://localhost:" + port);

        // 验证插件已启动
        if (pluginManager != null) {
            PluginStatusAssert.assertPlugin(pluginManager, "dev-mode-plugin")
                    .exists()
                    .isStarted();
        }
    }

    // ==================== HelloWorldHandler 测试 ====================

    @Test
    @DisplayName("HelloWorldHandler - /hello 默认参数")
    void testHelloWorld_defaultParam() {
        HttpResponse response = httpUtil.get("/plugin/hello-plugin/hello");

        response.assertSuccess()
                .assertJsonFieldExists("message")
                .assertJsonFieldExists("timestamp")
                .assertJsonFieldExists("plugin")
                .assertJsonFieldExists("loadSource")
                .assertJsonFieldExists("version");

        String message = response.getJsonField("message");
        assertThat(message).contains("hello, World!");

        log.info("✓ HelloWorldHandler /hello 默认参数测试通过");
    }

    @Test
    @DisplayName("HelloWorldHandler - /hello 自定义name参数")
    void testHelloWorld_customName() {
        Map<String, String> params = HelloWorldData.helloQueryParams("OneBase");
        HttpResponse response = httpUtil.get("/plugin/hello-plugin/hello", params);

        response.assertSuccess()
                .assertJsonFieldEquals("plugin", "hello-plugin-00135");

        String message = response.getJsonField("message");
        assertThat(message).contains("hello, OneBase!");

        log.info("✓ HelloWorldHandler /hello 自定义参数测试通过");
    }

    @Test
    @DisplayName("HelloWorldHandler - /process 正常数据")
    void testHelloWorld_process_success() {
        Map<String, Object> requestBody = HelloWorldData.processRequestBody("Test", 100);
        HttpResponse response = httpUtil.post("/plugin/hello-plugin/process", requestBody);

        response.assertSuccess()
                .assertJsonFieldExists("received")
                .assertJsonFieldExists("size")
                .assertJsonFieldExists("timestamp")
                .assertJsonFieldExists("plugin")
                .assertJsonFieldExists("message");

        Integer size = response.getJsonField("size");
        assertThat(size).isEqualTo(2);

        log.info("✓ HelloWorldHandler /process 正常数据测试通过");
    }

    @Test
    @DisplayName("HelloWorldHandler - /process 空数据")
    void testHelloWorld_process_emptyData() {
        Map<String, Object> requestBody = HelloWorldData.emptyProcessRequestBody();
        HttpResponse response = httpUtil.post("/plugin/hello-plugin/process", requestBody);

        response.assertSuccess();
        Integer size = response.getJsonField("size");
        assertThat(size).isEqualTo(0);

        log.info("✓ HelloWorldHandler /process 空数据测试通过");
    }

    // ==================== CYSTestController 测试 ====================

    @Test
    @DisplayName("CYSTestController - /cysinfo 默认参数")
    void testCYSTest_defaultParam() {
        HttpResponse response = httpUtil.get("/plugin/hello-plugin/cysinfo");

        response.assertSuccess()
                .assertJsonFieldExists("message")
                .assertJsonFieldExists("timestamp")
                .assertJsonFieldEquals("plugin", "hello-plugin");

        String message = response.getJsonField("message");
        assertThat(message).contains("this is cys test");

        log.info("✓ CYSTestController /cysinfo 默认参数测试通过");
    }

    @Test
    @DisplayName("CYSTestController - /cysinfo 自定义name参数")
    void testCYSTest_customName() {
        Map<String, String> params = CYSTestData.cysinfoQueryParams("TestUser");
        HttpResponse response = httpUtil.get("/plugin/hello-plugin/cysinfo", params);

        response.assertSuccess();
        String message = response.getJsonField("message");
        assertThat(message).contains("TestUser");

        log.info("✓ CYSTestController /cysinfo 自定义参数测试通过");
    }

    // ==================== CustomApiHandler 测试 ====================

    @Test
    @DisplayName("CustomApiHandler - /api/info 插件信息")
    void testCustomApi_info() {
        HttpResponse response = httpUtil.get("/plugin/hello-plugin/api/info");

        response.assertSuccess()
                .assertJsonFieldEquals("plugin", "hello-plugin")
                .assertJsonFieldEquals("version", "1.0.0")
                .assertJsonFieldEquals("springInjectionWorking", true);

        // 验证所有字段存在
        for (String field : ExpectedFields.pluginInfoResponseFields()) {
            response.assertJsonFieldExists(field);
        }

        log.info("✓ CustomApiHandler /api/info 测试通过");
    }

    @Test
    @DisplayName("CustomApiHandler - /api/status 插件状态")
    void testCustomApi_status() {
        HttpResponse response = httpUtil.get("/plugin/hello-plugin/api/status");

        response.assertSuccess()
                .assertJsonFieldEquals("status", "running");

        // 验证所有字段存在
        for (String field : ExpectedFields.statusResponseFields()) {
            response.assertJsonFieldExists(field);
        }

        log.info("✓ CustomApiHandler /api/status 测试通过");
    }

    @Test
    @DisplayName("CustomApiHandler - /api/process 正常数据")
    void testCustomApi_process_success() {
        Map<String, Object> requestBody = CustomApiData.sampleProcessData();
        HttpResponse response = httpUtil.post("/plugin/hello-plugin/api/process", requestBody);

        response.assertSuccess()
                .assertJsonFieldEquals("success", true);

        Integer processedCount = response.getJsonField("processedCount");
        assertThat(processedCount).isEqualTo(3);

        log.info("✓ CustomApiHandler /api/process 正常数据测试通过");
    }

    @Test
    @DisplayName("CustomApiHandler - /api/process 空数据")
    void testCustomApi_process_emptyData() {
        Map<String, Object> requestBody = CustomApiData.emptyProcessData();
        HttpResponse response = httpUtil.post("/plugin/hello-plugin/api/process", requestBody);

        response.assertSuccess()
                .assertJsonFieldEquals("success", true);

        Integer processedCount = response.getJsonField("processedCount");
        assertThat(processedCount).isEqualTo(0);

        log.info("✓ CustomApiHandler /api/process 空数据测试通过");
    }
}
