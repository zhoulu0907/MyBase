/**
 * TaskInstanceApi 连通性测试
 *
 * 覆盖点：
 * - GET /task-instance 分页
 * - GET /task-instances 分页
 * - GET /log/{projectCode}/detail 查询日志
 * - 校验 token Header、请求路径与返回 DTO 映射
 * - 支持真实服务器测试和本地模拟测试
 *
 * @author matianyu
 * @date 2025-10-16
 */
package com.cmsr.onebase.framework.connectivity;

import com.cmsr.onebase.framework.config.DolphinSchedulerClientAutoConfiguration;
import com.cmsr.onebase.framework.remote.TaskInstanceApi;
import com.cmsr.onebase.framework.remote.dto.HttpRestResultDTO;
import com.cmsr.onebase.framework.remote.dto.PageInfoDTO;
import com.cmsr.onebase.framework.remote.dto.taskinstance.TaskInstanceQueryRespDTO;
import com.cmsr.onebase.framework.config.DolphinSchedulerClientProperties;
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

@SpringBootTest
public class TaskInstanceApiConnectivityTest {

    @Autowired
    private DolphinSchedulerClientProperties properties;

    @Autowired(required = false)
    private TaskInstanceApi taskInstanceApi;

    @Test
    void testTaskInstanceApiConnectivity() throws Exception {
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
        Assertions.assertThat(taskInstanceApi).isNotNull();

        // 测试分页查询接口（使用一个不存在的processInstanceId，预期返回空列表但请求成功）
        Response<HttpRestResultDTO<PageInfoDTO<TaskInstanceQueryRespDTO>>> listResp =
                taskInstanceApi.listTaskInstances(1L, 1, 10, 999999999L).execute();

        // 断言远端服务器正常响应
        Assertions.assertThat(listResp.isSuccessful()).isTrue();
        Assertions.assertThat(listResp.body()).isNotNull();
        Assertions.assertThat(listResp.body().getCode()).isEqualTo(0);
        Assertions.assertThat(listResp.body().getMsg()).containsIgnoringCase("success");
    }

    /**
     * 本地模拟测试
     */
    private void testWithMockServer() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            // 1) /task-instance
            String listJson = "{\n" +
                    "  \"code\": 0,\n" +
                    "  \"msg\": \"success\",\n" +
                    "  \"data\": {\n" +
                    "    \"total\": 3,\n" +
                    "    \"pageNo\": 2,\n" +
                    "    \"pageSize\": 5,\n" +
                    "    \"records\": [ { \"id\": 4001, \"processInstanceId\": 5001, \"name\": \"TI-Test-1\", \"state\": \"RUNNING\" } ]\n" +
                    "  }\n" +
                    "}";
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(listJson));
            // 2) /task-instances
            String pageJson = "{\n" +
                    "  \"code\": 0,\n" +
                    "  \"msg\": \"success\",\n" +
                    "  \"data\": {\n" +
                    "    \"total\": 1,\n" +
                    "    \"pageNo\": 8,\n" +
                    "    \"pageSize\": 2,\n" +
                    "    \"records\": [ { \"id\": 4002, \"processInstanceId\": 5002, \"name\": \"TI-Test-2\", \"state\": \"SUCCESS\" } ]\n" +
                    "  }\n" +
                    "}";
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(pageJson));
            // 3) /log
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json")
                    .setBody("{\"code\":0,\"msg\":\"success\",\"data\":{\"log\":\"hello\"}}"));

            server.start();

            String base = server.url("/").toString();
            String token = "test-token";

            ApplicationContextRunner runner = new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(DolphinSchedulerClientAutoConfiguration.class))
                    .withPropertyValues(
                            "onebase.dolphinscheduler.client.enabled=true",
                            "onebase.dolphinscheduler.client.baseUrl=" + base,
                            "onebase.dolphinscheduler.client.token=" + token
                    );

            runner.run(context -> {
                Assertions.assertThat(context).hasNotFailed();
                TaskInstanceApi api = context.getBean(TaskInstanceApi.class);
                try {
                    Response<HttpRestResultDTO<PageInfoDTO<TaskInstanceQueryRespDTO>>> listResp =
                            api.listTaskInstances(222L, 2, 5, 123456L).execute();
                    Assertions.assertThat(listResp.isSuccessful()).isTrue();
                    Assertions.assertThat(listResp.body()).isNotNull();
                    Assertions.assertThat(listResp.body().getCode()).isEqualTo(0);
                    Assertions.assertThat(listResp.body().getMsg()).isEqualTo("success");
                    Assertions.assertThat(listResp.body().getData()).isNotNull();
                    Assertions.assertThat(listResp.body().getData().getTotal()).isEqualTo(3);

                    Response<HttpRestResultDTO<PageInfoDTO<TaskInstanceQueryRespDTO>>> pageResp =
                            api.page(222L, 8, 2, 654321L).execute();
                    Assertions.assertThat(pageResp.isSuccessful()).isTrue();
                    Assertions.assertThat(pageResp.body()).isNotNull();
                    Assertions.assertThat(pageResp.body().getCode()).isEqualTo(0);
                    Assertions.assertThat(pageResp.body().getMsg()).isEqualTo("success");
                    Assertions.assertThat(pageResp.body().getData()).isNotNull();
                    Assertions.assertThat(pageResp.body().getData().getRecords().get(0).getName()).isEqualTo("TI-Test-2");

                    Response<HttpRestResultDTO<Object>> logResp = api.queryLog(222L, 4002L, 0, 1024).execute();
                    Assertions.assertThat(logResp.isSuccessful()).isTrue();
                    Assertions.assertThat(logResp.body()).isNotNull();
                    Assertions.assertThat(logResp.body().getCode()).isEqualTo(0);
                    Assertions.assertThat(logResp.body().getMsg()).isEqualTo("success");

                    // 校验请求
                    RecordedRequest r1 = server.takeRequest();
                    Assertions.assertThat(r1.getMethod()).isEqualTo("GET");
                    Assertions.assertThat(r1.getPath()).contains("/projects/222/task-instance");
                    Assertions.assertThat(r1.getHeader("token")).isEqualTo(token);

                    RecordedRequest r2 = server.takeRequest();
                    Assertions.assertThat(r2.getMethod()).isEqualTo("GET");
                    Assertions.assertThat(r2.getPath()).contains("/projects/222/task-instances");

                    RecordedRequest r3 = server.takeRequest();
                    Assertions.assertThat(r3.getMethod()).isEqualTo("GET");
                    Assertions.assertThat(r3.getPath()).contains("/log/222/detail");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
