package com.cmsr.onebase.plugin.runtime.test.api;

import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.runtime.test.util.PluginHttpTestUtil;
import com.cmsr.onebase.plugin.runtime.test.util.PluginHttpTestUtil.HttpResponse;
import com.cmsr.onebase.plugin.runtime.test.util.PluginStatusAssert;
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

/**
 * PROD 模式 API 响应测试
 * <p>
 * 测试所有插件接口在 PROD 模式下的响应
 * 注意：需要在 plugins 目录下有实际的插件 ZIP 包
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-25
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(properties = {
        "onebase.plugin.enabled=true",
        "onebase.plugin.mode=prod",
        "onebase.plugin.auto-load=true",
        "onebase.plugin.auto-start=true",
        "onebase.plugin.plugins-dir=D:/cmsr/10_cmsr/CodingSpace/plugins-root"
})
public class ProdModeApiTest {

    private static final Logger log = LoggerFactory.getLogger(ProdModeApiTest.class);

    @LocalServerPort
    private int port;

    @Autowired(required = false)
    private OneBasePluginManager pluginManager;

    private PluginHttpTestUtil httpUtil;

    @BeforeEach
    void setUp() {
        httpUtil = new PluginHttpTestUtil("http://localhost:" + port);

        if (pluginManager != null && pluginManager.getPlugin("hello-plugin").isPresent()) {
            PluginStatusAssert.assertPlugin(pluginManager, "hello-plugin")
                    .exists()
                    .isStarted();
        } else {
            log.warn("PROD 模式下未找到 hello-plugin，请确保 plugins 目录下有插件包");
        }
    }

    @Test
    @DisplayName("PROD - HelloWorldHandler /hello 接口")
    void testHelloWorld() {
        if (pluginManager == null || !pluginManager.getPlugin("hello-plugin").isPresent()) {
            log.warn("跳过测试：插件未加载");
            return;
        }

        HttpResponse response = httpUtil.get("/plugin/hello-plugin/hello");
        response.assertSuccess()
                .assertJsonFieldExists("message")
                .assertJsonFieldExists("timestamp");

        log.info("✓ PROD 模式 HelloWorldHandler /hello 测试通过");
    }

    @Test
    @DisplayName("PROD - CustomApiHandler /api/info 接口")
    void testCustomApiInfo() {
        if (pluginManager == null || !pluginManager.getPlugin("hello-plugin").isPresent()) {
            log.warn("跳过测试：插件未加载");
            return;
        }

        HttpResponse response = httpUtil.get("/plugin/hello-plugin/api/info");
        response.assertSuccess()
                .assertJsonFieldEquals("plugin", "hello-plugin");

        log.info("✓ PROD 模式 CustomApiHandler /api/info 测试通过");
    }
}
