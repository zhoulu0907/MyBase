package com.cmsr.onebase.plugin.simulator.test.api;

import com.cmsr.onebase.plugin.simulator.test.util.PluginHttpTestUtil;
import com.cmsr.onebase.plugin.simulator.test.util.PluginHttpTestUtil.HttpResponse;
import com.cmsr.onebase.plugin.simulator.test.util.ContextDemoData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

/**
 * ContextDemoController API 测试
 * <p>
 * 测试插件上下文服务相关的 API 端点
 * </p>
 *
 * @author chengyuansen
 * @date 2026-01-10
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "onebase.plugin.enabled=true",
        "onebase.plugin.mode=dev",
        "onebase.plugin.auto-load=true",
        "onebase.plugin.auto-start=true",
        "onebase.plugin.dev-class-paths[0]=../onebase-plugin-demo/plugin-demo-hello/target/classes"
})
public class ContextDemoApiTest {

    private static final Logger log = LoggerFactory.getLogger(ContextDemoApiTest.class);

    @LocalServerPort
    private int port;

    private PluginHttpTestUtil httpUtil;

    @BeforeEach
    void setUp() {
        httpUtil = new PluginHttpTestUtil("http://localhost:" + port);
    }

    // ==================== ContextDemoController 测试 ====================

    @Test
    @DisplayName("ContextDemoController - /context/all 获取所有配置")
    void testContext_all() {
        HttpResponse response = httpUtil.get("/runtime/plugin/hello-plugin/context/all");

        response.assertSuccess();
        for (String field : ContextDemoData.contextAllResponseFields()) {
            response.assertJsonFieldExists(field);
        }

        log.info("✓ ContextDemoController /context/all 测试通过");
    }

    @Test
    @DisplayName("ContextDemoController - /context/current-tenant/tenantId 获取租户ID")
    void testContext_currentTenantId() {
        HttpResponse response = httpUtil.get("/runtime/plugin/hello-plugin/context/current-tenant/tenantId");

        response.assertSuccess();
        for (String field : ContextDemoData.contextTenantIdResponseFields()) {
            response.assertJsonFieldExists(field);
        }

        log.info("✓ ContextDemoController /context/current-tenant/tenantId 测试通过");
    }

    @Test
    @DisplayName("ContextDemoController - /context/key/{key} 获取指定配置项")
    void testContext_keyValue() {
        String key = ContextDemoData.sampleConfigKey();
        HttpResponse response = httpUtil.get("/runtime/plugin/hello-plugin/context/key/" + key);

        response.assertSuccess();
        for (String field : ContextDemoData.contextKeyValueResponseFields()) {
            response.assertJsonFieldExists(field);
        }

        log.info("✓ ContextDemoController /context/key/{} 测试通过", key);
    }

    @Test
    @DisplayName("ContextDemoController - /context/demo 配置应用演示")
    void testContext_demo() {
        HttpResponse response = httpUtil.get("/runtime/plugin/hello-plugin/context/demo");

        response.assertSuccess();
        for (String field : ContextDemoData.contextDemoResponseFields()) {
            response.assertJsonFieldExists(field);
        }

        log.info("✓ ContextDemoController /context/demo 测试通过");
    }

    @Test
    @DisplayName("ContextDemoController - /context/info 配置信息总览")
    void testContext_info() {
        HttpResponse response = httpUtil.get("/runtime/plugin/hello-plugin/context/info");

        response.assertSuccess();
        for (String field : ContextDemoData.contextInfoResponseFields()) {
            response.assertJsonFieldExists(field);
        }

        log.info("✓ ContextDemoController /context/info 测试通过");
    }
}
