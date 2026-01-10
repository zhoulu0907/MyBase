package com.cmsr.onebase.plugin.runtime.test.util;

import com.cmsr.onebase.plugin.runtime.test.util.PluginHttpTestUtil.HttpResponse;
import com.cmsr.onebase.plugin.runtime.test.util.PluginTestDataBuilder.*;
import org.slf4j.Logger;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 公共API测试方法集合
 * <p>
 * 提供可复用的测试方法，供 DevModeApiTest、StagingModeApiTest、ProdModeApiTest 调用。
 * 所有方法都是静态的，接收 PluginHttpTestUtil 和 Logger 参数。
 * </p>
 *
 * @author chengyuansen
 * @date 2026-01-10
 */
public class CommonApiTests {

    // ==================== HelloWorldHandler 测试 ====================

    /**
     * 测试 HelloWorldHandler - /hello 默认参数
     */
    public static void testHelloWorld_defaultParam(PluginHttpTestUtil httpUtil, Logger log) {
        HttpResponse response = httpUtil.get("/runtime/plugin/hello-plugin/hello");

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

    /**
     * 测试 HelloWorldHandler - /hello 自定义name参数
     */
    public static void testHelloWorld_customName(PluginHttpTestUtil httpUtil, Logger log) {
        Map<String, String> params = HelloWorldData.helloQueryParams("OneBase");
        HttpResponse response = httpUtil.get("/runtime/plugin/hello-plugin/hello", params);

        response.assertSuccess();

        String message = response.getJsonField("message");
        assertThat(message).contains("hello, OneBase!");

        log.info("✓ HelloWorldHandler /hello 自定义参数测试通过");
    }

    /**
     * 测试 HelloWorldHandler - /process 正常数据
     */
    public static void testHelloWorld_process_success(PluginHttpTestUtil httpUtil, Logger log) {
        Map<String, Object> requestBody = HelloWorldData.processRequestBody("Test", 100);
        HttpResponse response = httpUtil.post("/runtime/plugin/hello-plugin/process", requestBody);

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

    /**
     * 测试 HelloWorldHandler - /process 空数据
     */
    public static void testHelloWorld_process_emptyData(PluginHttpTestUtil httpUtil, Logger log) {
        Map<String, Object> requestBody = HelloWorldData.emptyProcessRequestBody();
        HttpResponse response = httpUtil.post("/runtime/plugin/hello-plugin/process", requestBody);

        response.assertSuccess();
        Integer size = response.getJsonField("size");
        assertThat(size).isEqualTo(0);

        log.info("✓ HelloWorldHandler /process 空数据测试通过");
    }

    // ==================== CYSTestController 测试 ====================

    /**
     * 测试 CYSTestController - /cysinfo 默认参数
     */
    public static void testCYSTest_defaultParam(PluginHttpTestUtil httpUtil, Logger log) {
        HttpResponse response = httpUtil.get("/runtime/plugin/hello-plugin/cysinfo");

        response.assertSuccess()
                .assertJsonFieldExists("message")
                .assertJsonFieldExists("timestamp")
                .assertJsonFieldEquals("plugin", "hello-plugin");

        String message = response.getJsonField("message");
        assertThat(message).contains("this is cys test");

        log.info("✓ CYSTestController /cysinfo 默认参数测试通过");
    }

    /**
     * 测试 CYSTestController - /cysinfo 自定义name参数
     */
    public static void testCYSTest_customName(PluginHttpTestUtil httpUtil, Logger log) {
        Map<String, String> params = CYSTestData.cysinfoQueryParams("TestUser");
        HttpResponse response = httpUtil.get("/runtime/plugin/hello-plugin/cysinfo", params);

        response.assertSuccess();
        String message = response.getJsonField("message");
        assertThat(message).contains("TestUser");

        log.info("✓ CYSTestController /cysinfo 自定义参数测试通过");
    }

    // ==================== ContextDemoController 测试 ====================

    /**
     * 测试 ContextDemoController - /context/all 获取所有配置
     */
    public static void testContext_all(PluginHttpTestUtil httpUtil, Logger log) {
        HttpResponse response = httpUtil.get("/runtime/plugin/hello-plugin/context/all");

        response.assertSuccess();
        for (String field : ContextDemoData.contextAllResponseFields()) {
            response.assertJsonFieldExists(field);
        }

        log.info("✓ ContextDemoController /context/all 测试通过");
    }

    /**
     * 测试 ContextDemoController - /context/current-tenant/tenantId 获取租户ID
     */
    public static void testContext_currentTenantId(PluginHttpTestUtil httpUtil, Logger log) {
        HttpResponse response = httpUtil.get("/runtime/plugin/hello-plugin/context/current-tenant/tenantId");

        response.assertSuccess();
        for (String field : ContextDemoData.contextTenantIdResponseFields()) {
            response.assertJsonFieldExists(field);
        }

        log.info("✓ ContextDemoController /context/current-tenant/tenantId 测试通过");
    }

    /**
     * 测试 ContextDemoController - /context/key/{key} 获取指定配置项
     */
    public static void testContext_keyValue(PluginHttpTestUtil httpUtil, Logger log) {
        String key = ContextDemoData.sampleConfigKey();
        HttpResponse response = httpUtil.get("/runtime/plugin/hello-plugin/context/key/" + key);

        response.assertSuccess();
        for (String field : ContextDemoData.contextKeyValueResponseFields()) {
            response.assertJsonFieldExists(field);
        }

        String responseKey = response.getJsonField("key");
        assertThat(responseKey).isEqualTo(key);

        log.info("✓ ContextDemoController /context/key/{} 测试通过", key);
    }

    /**
     * 测试 ContextDemoController - /context/demo 配置应用演示
     */
    public static void testContext_demo(PluginHttpTestUtil httpUtil, Logger log) {
        HttpResponse response = httpUtil.get("/runtime/plugin/hello-plugin/context/demo");

        response.assertSuccess();
        for (String field : ContextDemoData.contextDemoResponseFields()) {
            response.assertJsonFieldExists(field);
        }

        log.info("✓ ContextDemoController /context/demo 测试通过");
    }

    /**
     * 测试 ContextDemoController - /context/info 配置信息总览
     */
    public static void testContext_info(PluginHttpTestUtil httpUtil, Logger log) {
        HttpResponse response = httpUtil.get("/runtime/plugin/hello-plugin/context/info");

        response.assertSuccess();
        for (String field : ContextDemoData.contextInfoResponseFields()) {
            response.assertJsonFieldExists(field);
        }

        log.info("✓ ContextDemoController /context/info 测试通过");
    }

    // ==================== CustomApiHandler 测试 ====================

    /**
     * 测试 CustomApiHandler - /api/info 插件信息
     */
    public static void testCustomApi_info(PluginHttpTestUtil httpUtil, Logger log) {
        HttpResponse response = httpUtil.get("/runtime/plugin/hello-plugin/api/info");

        response.assertSuccess()
                .assertJsonFieldEquals("plugin", "hello-plugin")
                .assertJsonFieldEquals("version", "1.0.0")
                .assertJsonFieldEquals("springInjectionWorking", true);

        for (String field : ExpectedFields.pluginInfoResponseFields()) {
            response.assertJsonFieldExists(field);
        }

        log.info("✓ CustomApiHandler /api/info 测试通过");
    }

    /**
     * 测试 CustomApiHandler - /api/status 插件状态
     */
    public static void testCustomApi_status(PluginHttpTestUtil httpUtil, Logger log) {
        HttpResponse response = httpUtil.get("/runtime/plugin/hello-plugin/api/status");

        response.assertSuccess()
                .assertJsonFieldEquals("status", "running");

        for (String field : ExpectedFields.statusResponseFields()) {
            response.assertJsonFieldExists(field);
        }

        log.info("✓ CustomApiHandler /api/status 测试通过");
    }

    /**
     * 测试 CustomApiHandler - /api/process 正常数据
     */
    public static void testCustomApi_process_success(PluginHttpTestUtil httpUtil, Logger log) {
        Map<String, Object> requestBody = CustomApiData.sampleProcessData();
        HttpResponse response = httpUtil.post("/runtime/plugin/hello-plugin/api/process", requestBody);

        response.assertSuccess()
                .assertJsonFieldEquals("success", true);

        Integer processedCount = response.getJsonField("processedCount");
        assertThat(processedCount).isEqualTo(3);

        log.info("✓ CustomApiHandler /api/process 正常数据测试通过");
    }

    /**
     * 测试 CustomApiHandler - /api/process 空数据
     */
    public static void testCustomApi_process_emptyData(PluginHttpTestUtil httpUtil, Logger log) {
        Map<String, Object> requestBody = CustomApiData.emptyProcessData();
        HttpResponse response = httpUtil.post("/runtime/plugin/hello-plugin/api/process", requestBody);

        response.assertSuccess()
                .assertJsonFieldEquals("success", true);

        Integer processedCount = response.getJsonField("processedCount");
        assertThat(processedCount).isEqualTo(0);

        log.info("✓ CustomApiHandler /api/process 空数据测试通过");
    }

    // ==================== HutoolCryptoHandler 测试 ====================

    /**
     * 测试 HutoolCryptoHandler - /crypto 加密功能
     */
    public static void testHutool_crypto(PluginHttpTestUtil httpUtil, Logger log) {
        Map<String, String> params = HutoolCryptoData.cryptoQueryParams(HutoolCryptoData.customText());
        HttpResponse response = httpUtil.get("/runtime/plugin/hello-plugin/crypto", params);

        response.assertSuccess()
                .assertJsonFieldEquals("success", true)
                .assertJsonFieldEquals("hutoolLoaded", true)
                .assertJsonFieldEquals("aesVerified", true);

        for (String field : ExpectedFields.cryptoResponseFields()) {
            response.assertJsonFieldExists(field);
        }

        log.info("✓ HutoolCryptoHandler /crypto 测试通过");
    }

    /**
     * 测试 HutoolCryptoHandler - /check-hutool 检查Hutool依赖
     */
    public static void testHutool_checkHutool(PluginHttpTestUtil httpUtil, Logger log) {
        HttpResponse response = httpUtil.get("/runtime/plugin/hello-plugin/check-hutool");

        response.assertSuccess()
                .assertJsonFieldEquals("success", true)
                .assertJsonFieldEquals("hutoolClassLoaded", true)
                .assertJsonFieldEquals("md5Correct", true);

        for (String field : ExpectedFields.checkHutoolResponseFields()) {
            response.assertJsonFieldExists(field);
        }

        log.info("✓ HutoolCryptoHandler /check-hutool 测试通过");
    }

    // ==================== TestHttpHandler 测试 ====================

    /**
     * 测试 TestHttpHandler - /api/info 测试插件信息
     */
    public static void testTestPlugin_info(PluginHttpTestUtil httpUtil, Logger log) {
        HttpResponse response = httpUtil.get("/runtime/plugin/test-plugin/api/info");

        response.assertSuccess()
                .assertJsonFieldEquals("plugin", "plugin-demo-test")
                .assertJsonFieldEquals("version", "1.0.0");

        for (String field : TestPluginData.testPluginInfoResponseFields()) {
            response.assertJsonFieldExists(field);
        }

        String message = response.getJsonField("message");
        assertThat(message).contains("测试demo");

        log.info("✓ TestHttpHandler /api/info 测试通过");
    }
}
