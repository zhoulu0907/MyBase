package com.cmsr.onebase.plugin.simulator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 接入onebase-server-runtime时，用于测试2个示例插件的14个API接口。
 * 前提：必须启动onebase-server-runtime服务，且已安装2个示例插件。登录信息配置在代码中。
 * <p>
 * 自动化执行流程：
 * 1. 登录获取 AccessToken
 * 2. 依次测试 14 个插件接口
 * </p>
 *
 * @author chengyuansen
 * @date 2026-01-10
 */

public class RuntimeModePluginDemoApiTest {

    private static final Logger log = LoggerFactory.getLogger(RuntimeModePluginDemoApiTest.class);

    // ==================== 1. 配置参数 ====================
    private static final String BASE_URL = "http://127.0.0.1:48081/runtime";
    private static final String APP_ID = "166917874067439616";
    private static final String TENANT_ID = "158088726951460864";

    // 登录参数
    private static final String USERNAME = "gaoguoqing";
    private static final String PASSWORD = "124f54e7508c286e1e488e75bc3a80d4a999fa13a2a1646d7ddce30eaaa52f2198131554b6194a52a793ec03aaf94b7e8c841636beea488831b08d8678ed3cee4f95ed83ac683d72ca51252ffecfb6bd9193d981981ff15b0c27c5d7183035ba7c7c235f48827ba27ddbb4bcec3b71";
    private static final String CAPTCHA = "3oJFZk4xdNNjawBg8JwIwkbZv6hhC6yKrjtBq7D7tbCABbnNmu8f0T5qf/LeSpLu1dhHwA+5nS1w+uD7AK6k72E8KSX5zeXQoO1BnNR7iJ0=";
    private static final String DEVICE_ID = "f3b000916478e376e7253f5694a8d1e8";

    // 工具实例
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testPluginApiFlow() {
        try {
            log.info("=========================================");
            log.info("   OneBase Plugin API Integration Tester ");
            log.info("=========================================\n");

            // 1. 执行登录
            String accessToken = login();
            Assertions.assertNotNull(accessToken, "❌ 登录失败，终止测试");
            log.info("✅ 登录成功");
            log.info("Token: {}...\n", accessToken);

            // 2. 准备测试任务
            Map<String, ApiRequest> requests = buildRequests();

            // 3. 执行测试
            log.info("=========================================");
            log.info("   开始执行 14 个接口测试 ");
            log.info("=========================================\n");

            int successCount = 0;
            for (Map.Entry<String, ApiRequest> entry : requests.entrySet()) {
                String name = entry.getKey();
                ApiRequest req = entry.getValue();

                boolean success = executeTest(name, req, accessToken);
                if (success)
                    successCount++;

                // 简单的延时，避免请求过快
                Thread.sleep(200);
            }

            // 4. 总结
            log.info("\n=========================================");
            log.info("测试完成: 成功 {} / 总计 {}", successCount, requests.size());
            log.info("=========================================");

            Assertions.assertEquals(requests.size(), successCount, "Not all API requests succeeded");

        } catch (Exception e) {
            log.error("测试异常", e);
            Assertions.fail(e.getMessage());
        }
    }

    /**
     * 登录获取 Token
     */
    private static String login() throws Exception {
        String url = BASE_URL + "/system/auth/app-login";

        // 构建登录参数
        Map<String, Object> loginParams = new LinkedHashMap<>();
        loginParams.put("password", PASSWORD);
        loginParams.put("username", USERNAME);
        loginParams.put("appId", APP_ID);
        loginParams.put("captchaVerification", CAPTCHA);
        loginParams.put("deviceId", DEVICE_ID);
        loginParams.put("loginPlatform", "mobile");

        String jsonBody = objectMapper.writeValueAsString(loginParams);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("X-Tenant-Id", TENANT_ID)
                .header("tag", "OneBase.gateway")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        log.info("🔄 正在登录...");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.error("❌ Login Failed: HTTP Status {}", response.statusCode());
            return null;
        }

        JsonNode root = objectMapper.readTree(response.body());
        int code = root.path("code").asInt(-1);

        // 断言：业务状态码 code 必须为 0
        if (code == 0) {
            log.info("✅ Login Success (Assert Code == 0)");
            return root.path("data").path("accessToken").asText();
        } else {
            log.error("❌ Login Failed (Assert Code == 0, Actual: {}): {}", code, root.path("msg").asText());
            return null;
        }
    }

    /**
     * 执行单个接口测试
     */
    private static boolean executeTest(String name, ApiRequest req, String token) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + req.path))
                    .header("Content-Type", "application/json")
                    .header("X-Application-Id", APP_ID)
                    .header("Authorization", token);

            if ("POST".equals(req.method)) {
                String body = req.body != null ? objectMapper.writeValueAsString(req.body) : "{}";
                builder.POST(HttpRequest.BodyPublishers.ofString(body));
            } else {
                builder.GET();
            }

            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

            // 核心断言：状态码必须为 200
            if (response.statusCode() == 200) {
                log.info("测试 [{}] {} ... ✅ OK (Assert Status == 200)", req.method, name);
                return true;
            } else {
                log.error("测试 [{}] {} ... ❌ FAILED (Assert Status == 200, Actual: {})", req.method, name,
                        response.statusCode());
                log.error("   Response: {}", response.body());
                return false;
            }

        } catch (Exception e) {
            log.error("测试 [{}] {} ... ❌ ERROR: {}", req.method, name, e.getMessage());
            return false;
        }
    }

    private static Map<String, ApiRequest> buildRequests() {
        Map<String, ApiRequest> map = new LinkedHashMap<>();

        // CustomApiHandler
        map.put("CustomApi - Info", new ApiRequest("GET", "/plugin/hello-plugin/api/info"));
        map.put("CustomApi - Status", new ApiRequest("GET", "/plugin/hello-plugin/api/status"));
        map.put("CustomApi - Process",
                new ApiRequest("POST", "/plugin/hello-plugin/api/process", Map.of("name", "Test", "value", 1)));

        // HelloWorldHandler
        map.put("Hello - World", new ApiRequest("GET", "/plugin/hello-plugin/hello?name=World"));
        map.put("Hello - Process",
                new ApiRequest("POST", "/plugin/hello-plugin/process", Map.of("name", "OneBase", "value", 100)));

        // HutoolCryptoHandler
        map.put("Hutool - Crypto", new ApiRequest("GET", "/plugin/hello-plugin/crypto?text=Hello"));
        map.put("Hutool - Check", new ApiRequest("GET", "/plugin/hello-plugin/check-hutool"));

        // TestHttpHandler
        map.put("TestPlugin - Info", new ApiRequest("GET", "/plugin/test-plugin/api/info"));

        // CYSTestController
        map.put("CYS - Info", new ApiRequest("GET", "/plugin/hello-plugin/cysinfo?name=World"));

        // ContextDemoController
        map.put("Context - All", new ApiRequest("GET", "/plugin/hello-plugin/context/all"));
        map.put("Context - Tenant", new ApiRequest("GET", "/plugin/hello-plugin/context/current-tenant/tenantId"));
        map.put("Context - Key", new ApiRequest("GET", "/plugin/hello-plugin/context/key/demoKey"));
        map.put("Context - Demo", new ApiRequest("GET", "/plugin/hello-plugin/context/demo"));
        map.put("Context - Info", new ApiRequest("GET", "/plugin/hello-plugin/context/info"));

        return map;
    }

    static class ApiRequest {
        String method;
        String path;
        Object body;

        public ApiRequest(String method, String path) {
            this(method, path, null);
        }

        public ApiRequest(String method, String path, Object body) {
            this.method = method;
            this.path = path;
            this.body = body;
        }
    }
}
