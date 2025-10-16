package com.cmsr.onebase.framework.connectivity;

import com.cmsr.onebase.framework.config.DolphinSchedulerClientAutoConfiguration;
import com.cmsr.onebase.framework.config.DolphinSchedulerClientProperties;
import com.cmsr.onebase.framework.remote.ProjectApi;
import com.cmsr.onebase.framework.remote.dto.HttpRestResultDTO;
import com.cmsr.onebase.framework.remote.dto.project.ProjectRespDTO;
import com.cmsr.onebase.framework.remote.dto.project.ProjectUpdateReqDTO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import retrofit2.Response;

/**
 * ProjectApi 连通性测试
 *
 * 覆盖点：
 * - 查询项目信息
 * - 更新项目
 * - 删除项目
 * - 校验 token Header、路径与 DTO 映射
 * - 支持真实服务器测试和本地模拟测试
 *
 * @author matianyu
 * @date 2025-10-16
 */
@SpringBootTest
public class ProjectApiConnectivityTest {

    @Autowired
    private DolphinSchedulerClientProperties properties;

    @Autowired(required = false)
    private ProjectApi projectApi;

    @Test
    void testProjectApiConnectivity() throws Exception {
        if (!properties.isEnabled()) {
            return;
        }

        if (properties.isEnableLiveConnectivityTest()) {
            testWithRealServer();
        } else {
            testWithMockServer();
        }
    }

    /**
     * 真实服务器测试
     */
    private void testWithRealServer() throws Exception {
        Assertions.assertThat(projectApi).isNotNull();

        System.out.println("=== ProjectApi 真实服务器测试开始 ===");
        System.out.println("DolphinScheduler URL: " + properties.getBaseUrl());
        System.out.println("Token: " + properties.getToken().substring(0, 8) + "...");

        Response<HttpRestResultDTO<ProjectRespDTO>> queryResp = null;
        try {
            // 测试查询项目接口
            queryResp = projectApi.queryProjectByCode(1L).execute();
        } catch (Exception e) {
            System.out.println("✗ 请求执行失败: " + e.getClass().getSimpleName());
            System.out.println("  错误信息: " + e.getMessage());

            if (e.getCause() != null && e.getCause().getMessage() != null) {
                String causeMsg = e.getCause().getMessage();
                if (causeMsg.contains("JsonParseException") || causeMsg.contains("Unexpected character")) {
                    System.out.println("\n⚠️ 服务器返回的不是JSON格式!");
                    System.out.println("可能原因:");
                    System.out.println("  1. Token无效或已过期,服务器返回了登录页面");
                    System.out.println("  2. API路径错误");
                    System.out.println("  3. DolphinScheduler版本不匹配");
                    System.out.println("\n请参考TROUBLESHOOTING.md文档进行问题排查");
                }
            }
            throw e;
        }

        // 输出诊断信息
        System.out.println("\n=== 响应信息 ===");
        System.out.println("HTTP状态码: " + queryResp.code());
        System.out.println("是否成功: " + queryResp.isSuccessful());

        if (!queryResp.isSuccessful()) {
            System.out.println("\n✗ HTTP请求失败!");
            System.out.println("错误响应体:");
            if (queryResp.errorBody() != null) {
                String errorContent = queryResp.errorBody().string();
                System.out.println(errorContent.substring(0, Math.min(500, errorContent.length())));
            }
            Assertions.fail("HTTP请求失败,状态码: " + queryResp.code());
        }

        if (queryResp.body() == null) {
            System.out.println("\n⚠️ 警告: 响应体为null,服务器可能返回了非JSON内容");
            System.out.println("响应头:");
            queryResp.headers().forEach(header ->
                    System.out.println("  " + header.getFirst() + ": " + header.getSecond())
            );
            System.out.println("\n可能原因:");
            System.out.println("  1. Token认证失败,返回了HTML登录页面");
            System.out.println("  2. API版本不匹配");
            System.out.println("请参考TROUBLESHOOTING.md文档进行问题排查");
            Assertions.fail("响应体为null,服务器返回了非JSON内容(如HTML登录页面)");
        }

        // 断言远端服务器正常响应
        Assertions.assertThat(queryResp.isSuccessful()).isTrue();
        Assertions.assertThat(queryResp.body()).isNotNull();
        Assertions.assertThat(queryResp.body().getCode()).isEqualTo(0);
        Assertions.assertThat(queryResp.body().getMsg()).containsIgnoringCase("success");

        System.out.println("✓ 查询项目接口测试通过");
        System.out.println("项目信息: " + queryResp.body().getData());
    }

    /**
     * 本地模拟测试
     */
    private void testWithMockServer() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.start();
            String base = server.url("/").toString();
            String token = "test-token-12345678";

            ApplicationContextRunner runner = new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(DolphinSchedulerClientAutoConfiguration.class))
                    .withPropertyValues(
                            "onebase.dolphinscheduler.client.enabled=true",
                            "onebase.dolphinscheduler.client.baseUrl=" + base,
                            "onebase.dolphinscheduler.client.token=" + token
                    );

            runner.run(context -> {
                Assertions.assertThat(context).hasNotFailed();
                ProjectApi api = context.getBean(ProjectApi.class);

                // 测试查询项目
                testQueryProject(server, api, token);

                // 测试更新项目
                testUpdateProject(server, api, token);

                // 测试删除项目
                testDeleteProject(server, api, token);
            });
        }
    }

    /**
     * 测试查询项目
     */
    private void testQueryProject(MockWebServer server, ProjectApi api, String token) throws Exception {
        // 构造查询响应数据
        String queryBody = "{\n" +
                "  \"code\": 0,\n" +
                "  \"msg\": \"success\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 1,\n" +
                "    \"userId\": 100,\n" +
                "    \"userName\": \"testUser\",\n" +
                "    \"code\": 123456789,\n" +
                "    \"name\": \"测试项目\",\n" +
                "    \"description\": \"这是一个测试项目\",\n" +
                "    \"createTime\": \"2025-10-16T10:00:00\",\n" +
                "    \"updateTime\": \"2025-10-16T12:00:00\",\n" +
                "    \"perm\": 7,\n" +
                "    \"defCount\": 10\n" +
                "  }\n" +
                "}";
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(queryBody));

        // 执行查询
        Response<HttpRestResultDTO<ProjectRespDTO>> queryResp =
                api.queryProjectByCode(123456789L).execute();

        // 断言响应
        Assertions.assertThat(queryResp.isSuccessful()).isTrue();
        HttpRestResultDTO<ProjectRespDTO> wrapper = queryResp.body();
        Assertions.assertThat(wrapper).isNotNull();
        Assertions.assertThat(wrapper.getCode()).isEqualTo(0);
        Assertions.assertThat(wrapper.getMsg()).isEqualTo("success");

        ProjectRespDTO project = wrapper.getData();
        Assertions.assertThat(project).isNotNull();
        Assertions.assertThat(project.getId()).isEqualTo(1);
        Assertions.assertThat(project.getUserId()).isEqualTo(100);
        Assertions.assertThat(project.getUserName()).isEqualTo("testUser");
        Assertions.assertThat(project.getCode()).isEqualTo(123456789L);
        Assertions.assertThat(project.getName()).isEqualTo("测试项目");
        Assertions.assertThat(project.getDescription()).isEqualTo("这是一个测试项目");
        Assertions.assertThat(project.getPerm()).isEqualTo(7);
        Assertions.assertThat(project.getDefCount()).isEqualTo(10);

        // 校验请求
        RecordedRequest req = server.takeRequest();
        Assertions.assertThat(req.getMethod()).isEqualTo("GET");
        Assertions.assertThat(req.getPath()).isEqualTo("/v2/projects/123456789");
        Assertions.assertThat(req.getHeader("token")).isEqualTo(token);

        System.out.println("✓ 查询项目接口模拟测试通过");
    }

    /**
     * 测试更新项目
     */
    private void testUpdateProject(MockWebServer server, ProjectApi api, String token) throws Exception {
        // 构造更新响应数据
        String updateBody = "{\n" +
                "  \"code\": 0,\n" +
                "  \"msg\": \"success\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 1,\n" +
                "    \"userId\": 100,\n" +
                "    \"userName\": \"testUser\",\n" +
                "    \"code\": 123456789,\n" +
                "    \"name\": \"更新后的项目名称\",\n" +
                "    \"description\": \"更新后的项目描述\",\n" +
                "    \"createTime\": \"2025-10-16T10:00:00\",\n" +
                "    \"updateTime\": \"2025-10-16T13:00:00\",\n" +
                "    \"perm\": 7,\n" +
                "    \"defCount\": 10\n" +
                "  }\n" +
                "}";
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(updateBody));

        // 构造更新请求参数
        ProjectUpdateReqDTO updateReq = new ProjectUpdateReqDTO();
        updateReq.setProjectName("更新后的项目名称");
        updateReq.setDescription("更新后的项目描述");

        // 执行更新
        Response<HttpRestResultDTO<ProjectRespDTO>> updateResp =
                api.updateProject(123456789L, updateReq).execute();

        // 断言响应
        Assertions.assertThat(updateResp.isSuccessful()).isTrue();
        HttpRestResultDTO<ProjectRespDTO> wrapper = updateResp.body();
        Assertions.assertThat(wrapper).isNotNull();
        Assertions.assertThat(wrapper.getCode()).isEqualTo(0);
        Assertions.assertThat(wrapper.getMsg()).isEqualTo("success");

        ProjectRespDTO project = wrapper.getData();
        Assertions.assertThat(project).isNotNull();
        Assertions.assertThat(project.getName()).isEqualTo("更新后的项目名称");
        Assertions.assertThat(project.getDescription()).isEqualTo("更新后的项目描述");

        // 校验请求
        RecordedRequest req = server.takeRequest();
        Assertions.assertThat(req.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(req.getPath()).isEqualTo("/v2/projects/123456789");
        Assertions.assertThat(req.getHeader("token")).isEqualTo(token);
        Assertions.assertThat(req.getHeader("Content-Type")).contains("application/json");

        // 校验请求体
        String requestBody = req.getBody().readUtf8();
        Assertions.assertThat(requestBody).contains("更新后的项目名称");
        Assertions.assertThat(requestBody).contains("更新后的项目描述");

        System.out.println("✓ 更新项目接口模拟测试通过");
    }

    /**
     * 测试删除项目
     */
    private void testDeleteProject(MockWebServer server, ProjectApi api, String token) throws Exception {
        // 构造删除响应数据
        String deleteBody = "{\n" +
                "  \"code\": 0,\n" +
                "  \"msg\": \"success\",\n" +
                "  \"data\": true\n" +
                "}";
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(deleteBody));

        // 执行删除
        Response<HttpRestResultDTO<Boolean>> deleteResp =
                api.deleteProject(123456789L).execute();

        // 断言响应
        Assertions.assertThat(deleteResp.isSuccessful()).isTrue();
        HttpRestResultDTO<Boolean> wrapper = deleteResp.body();
        Assertions.assertThat(wrapper).isNotNull();
        Assertions.assertThat(wrapper.getCode()).isEqualTo(0);
        Assertions.assertThat(wrapper.getMsg()).isEqualTo("success");
        Assertions.assertThat(wrapper.getData()).isTrue();

        // 校验请求
        RecordedRequest req = server.takeRequest();
        Assertions.assertThat(req.getMethod()).isEqualTo("DELETE");
        Assertions.assertThat(req.getPath()).isEqualTo("/v2/projects/123456789");
        Assertions.assertThat(req.getHeader("token")).isEqualTo(token);

        System.out.println("✓ 删除项目接口模拟测试通过");
    }
}
