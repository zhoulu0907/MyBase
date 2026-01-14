package com.cmsr.onebase.plugin.simulator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 接入onebase-server-runtime时，用于测试2个示例插件的14个API接口 + 一个OCR插件的3个接口。
 * 前提：必须启动onebase-server-runtime服务，且已安装2个示例插件+OCR插件。登录信息配置在代码中。
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
    private static final String TEST_IMAGES_PATH = "/test-images/";

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
            log.info("Token: {}\n", accessToken);

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

    // ==================== OCR 插件测试方法 ====================

    /**
     * 测试身份证识别接口
     */
    @Test
    public void testOcrIdCardRecognition() throws Exception {
        System.out.println("\n========== 测试 OCR 插件 - 身份证识别接口 ==========");

        // 1. 登录获取 Token
        String accessToken = login();
        Assertions.assertNotNull(accessToken, "❌ 登录失败，终止测试");
        log.info("✅ 登录成功\n");

        // 2. 从classpath加载测试文件
        File idCardFile = getResourceFile(TEST_IMAGES_PATH + "ID正.png");
        if (idCardFile == null || !idCardFile.exists()) {
            System.err.println("测试文件不存在: " + TEST_IMAGES_PATH + "ID正.png");
            return;
        }

        // 3. 执行请求
        String url = BASE_URL + "/plugin/onebase-plugin-ocr/id-card";
        Map<String, Object> files = new HashMap<>();
        files.put("frontFile", idCardFile);

        executeOcrMultipartRequest(url, files, null, accessToken);
    }

    /**
     * 测试港澳台通行证识别接口
     */
    @Test
    public void testOcrExitentrypermitRecognition() throws Exception {
        System.out.println("\n========== 测试 OCR 插件 - 港澳台通行证识别接口 ==========");

        // 1. 登录获取 Token
        String accessToken = login();
        Assertions.assertNotNull(accessToken, "❌ 登录失败，终止测试");
        log.info("✅ 登录成功\n");

        // 2. 从classpath加载测试文件
        File permitFile = getResourceFile(TEST_IMAGES_PATH + "台湾护照.jpg");
        if (permitFile == null || !permitFile.exists()) {
            System.err.println("测试文件不存在: " + TEST_IMAGES_PATH + "台湾护照.jpg");
            return;
        }

        // 3. 执行请求
        String url = BASE_URL + "/plugin/onebase-plugin-ocr/exitentrypermit";
        Map<String, Object> files = new HashMap<>();
        files.put("frontFile", permitFile);
        Map<String, String> params = new HashMap<>();
        params.put("exitentrypermitType", "tw_passport");

        executeOcrMultipartRequest(url, files, params, accessToken);
    }

    /**
     * 测试护照识别接口
     */
    @Test
    public void testOcrPassportRecognition() throws Exception {
        System.out.println("\n========== 测试 OCR 插件 - 护照识别接口 ==========");

        // 1. 登录获取 Token
        String accessToken = login();
        Assertions.assertNotNull(accessToken, "❌ 登录失败，终止测试");
        log.info("✅ 登录成功\n");

        // 2. 从classpath加载测试文件
        File passportFile = getResourceFile(TEST_IMAGES_PATH + "中国护照.jpg");
        if (passportFile == null || !passportFile.exists()) {
            System.err.println("测试文件不存在: " + TEST_IMAGES_PATH + "中国护照.jpg");
            return;
        }

        // 3. 执行请求
        String url = BASE_URL + "/plugin/onebase-plugin-ocr/passport";
        Map<String, Object> files = new HashMap<>();
        files.put("file", passportFile);

        executeOcrMultipartRequest(url, files, null, accessToken);
    }

    /**
     * 执行 OCR multipart/form-data 请求 (新版本 - 使用 File 对象)
     */
    private void executeOcrMultipartRequest(String url, Map<String, Object> files, Map<String, String> params,
            String accessToken)
            throws Exception {
        String boundary = "----WebKitFormBoundary" + UUID.randomUUID().toString().replace("-", "");

        // 只处理第一个文件
        Map.Entry<String, Object> fileEntry = files.entrySet().iterator().next();
        File file = (File) fileEntry.getValue();
        byte[] fileBytes = Files.readAllBytes(file.toPath());

        // 构建请求体开始部分
        StringBuilder bodyStart = new StringBuilder();
        bodyStart.append("--").append(boundary).append("\r\n");
        bodyStart.append("Content-Disposition: form-data; name=\"").append(fileEntry.getKey())
                .append("\"; filename=\"").append(file.getName()).append("\"\r\n");
        bodyStart.append("Content-Type: image/jpeg\r\n\r\n");

        // 构建请求体结束部分
        StringBuilder bodyEnd = new StringBuilder();
        bodyEnd.append("\r\n");

        // 添加额外参数
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                bodyEnd.append("--").append(boundary).append("\r\n");
                bodyEnd.append("Content-Disposition: form-data; name=\"").append(param.getKey())
                        .append("\"\r\n\r\n");
                bodyEnd.append(param.getValue()).append("\r\n");
            }
        }

        bodyEnd.append("--").append(boundary).append("--\r\n");

        // 组合完整的请求体
        byte[] bodyStartBytes = bodyStart.toString().getBytes(StandardCharsets.UTF_8);
        byte[] bodyEndBytes = bodyEnd.toString().getBytes(StandardCharsets.UTF_8);
        byte[] fullBody = new byte[bodyStartBytes.length + fileBytes.length + bodyEndBytes.length];
        System.arraycopy(bodyStartBytes, 0, fullBody, 0, bodyStartBytes.length);
        System.arraycopy(fileBytes, 0, fullBody, bodyStartBytes.length, fileBytes.length);
        System.arraycopy(bodyEndBytes, 0, fullBody, bodyStartBytes.length + fileBytes.length, bodyEndBytes.length);

        // 发送请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("X-Application-Id", APP_ID)
                .header("X-Tenant-Id", TENANT_ID)
                .header("Authorization", accessToken)
                .POST(HttpRequest.BodyPublishers.ofByteArray(fullBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 验证响应
        System.out.println("Response Status: " + response.statusCode());
        System.out.println("Response Body: " + response.body());

        if (response.statusCode() == 200) {
            JsonNode root = objectMapper.readTree(response.body());
            int code = root.path("code").asInt(-1);

            if (code == 0) {
                System.out.println("✅ 测试通过 (code == 0)");
            } else {
                throw new AssertionError("测试失败: code = " + code);
            }
        } else {
            throw new AssertionError("HTTP请求失败: " + response.statusCode());
        }
    }

    /**
     * 执行 OCR multipart/form-data 请求
     *
     * @param name             测试名称
     * @param path             请求路径
     * @param fileParamName    文件参数名
     * @param filePath         文件路径
     * @param additionalParams 额外的表单参数
     * @param token            访问令牌
     * @return 是否成功
     */
    private static boolean executeOcrMultipartRequest(
            String name,
            String path,
            String fileParamName,
            Path filePath,
            Map<String, String> additionalParams,
            String token) {
        try {
            String boundary = "----WebKitFormBoundary" + UUID.randomUUID().toString().replace("-", "");
            byte[] fileBytes = Files.readAllBytes(filePath);
            String fileName = filePath.getFileName().toString();

            // 构建 multipart/form-data 请求体
            StringBuilder bodyBuilder = new StringBuilder();

            // 添加文件部分
            bodyBuilder.append("--").append(boundary).append("\r\n");
            bodyBuilder.append("Content-Disposition: form-data; name=\"").append(fileParamName)
                    .append("\"; filename=\"").append(fileName).append("\"\r\n");
            bodyBuilder.append("Content-Type: image/jpeg\r\n\r\n");

            // 将文件内容转换为字符串（这里需要特殊处理）
            String bodyStart = bodyBuilder.toString();
            bodyBuilder = new StringBuilder();
            bodyBuilder.append("\r\n");

            // 添加额外参数
            if (additionalParams != null && !additionalParams.isEmpty()) {
                for (Map.Entry<String, String> entry : additionalParams.entrySet()) {
                    bodyBuilder.append("--").append(boundary).append("\r\n");
                    bodyBuilder.append("Content-Disposition: form-data; name=\"").append(entry.getKey())
                            .append("\"\r\n\r\n");
                    bodyBuilder.append(entry.getValue()).append("\r\n");
                }
            }

            bodyBuilder.append("--").append(boundary).append("--\r\n");
            String bodyEnd = bodyBuilder.toString();

            // 组合完整的请求体
            byte[] bodyStartBytes = bodyStart.getBytes(StandardCharsets.UTF_8);
            byte[] bodyEndBytes = bodyEnd.getBytes(StandardCharsets.UTF_8);
            byte[] fullBody = new byte[bodyStartBytes.length + fileBytes.length + bodyEndBytes.length];
            System.arraycopy(bodyStartBytes, 0, fullBody, 0, bodyStartBytes.length);
            System.arraycopy(fileBytes, 0, fullBody, bodyStartBytes.length, fileBytes.length);
            System.arraycopy(bodyEndBytes, 0, fullBody, bodyStartBytes.length + fileBytes.length,
                    bodyEndBytes.length);

            // 发送请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .header("X-Application-Id", APP_ID)
                    .header("Authorization", token)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(fullBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 验证响应
            if (response.statusCode() == 200) {
                // 解析响应，检查 code 字段
                JsonNode root = objectMapper.readTree(response.body());
                int code = root.path("code").asInt(-1);

                if (code == 0) {
                    log.info("测试 [POST] {} ... ✅ OK (Assert Code == 0)", name);
                    return true;
                } else {
                    log.error("测试 [POST] {} ... ❌ FAILED (Assert Code == 0, Actual: {})", name, code);
                    log.error("   Response: {}", response.body());
                    return false;
                }
            } else {
                log.error("测试 [POST] {} ... ❌ FAILED (Assert Status == 200, Actual: {})", name,
                        response.statusCode());
                log.error("   Response: {}", response.body());
                return false;
            }

        } catch (Exception e) {
            log.error("测试 [POST] {} ... ❌ ERROR: {}", name, e.getMessage());
            return false;
        }
    }

    /**
     * 从classpath加载资源文件
     */
    private File getResourceFile(String resourcePath) {
        try {
            return new File(getClass().getResource(resourcePath).toURI());
        } catch (Exception e) {
            System.err.println("无法加载资源文件: " + resourcePath + ", 错误: " + e.getMessage());
            return null;
        }
    }
}
