/**
 * TaskApi 连通性测试
 *
 * 覆盖点：
 * - GET listByProcessDefinitionCode 请求路径与鉴权头
 * - HttpRestResultDTO<List<TaskDefinitionRespDTO>> 映射
 * - 支持真实服务器测试和本地模拟测试
 *
 * @author matianyu
 * @date 2025-10-16
 */
package com.cmsr.onebase.framework.connectivity;

import com.cmsr.onebase.framework.config.DolphinSchedulerClientAutoConfiguration;
import com.cmsr.onebase.framework.config.DolphinSchedulerClientProperties;
import com.cmsr.onebase.framework.remote.TaskApi;
import com.cmsr.onebase.framework.remote.dto.HttpRestResultDTO;
import com.cmsr.onebase.framework.remote.dto.task.TaskDefinitionRespDTO;
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

import java.util.List;

@SpringBootTest
public class TaskApiConnectivityTest {

    @Autowired
    private DolphinSchedulerClientProperties properties;

    @Autowired(required = false)
    private TaskApi taskApi;

    @Test
    void testTaskApiConnectivity() throws Exception {
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
        Assertions.assertThat(taskApi).isNotNull();

        // 测试查询任务定义列表接口（使用一个不存在的code，预期返回空列表但请求成功）
        Response<HttpRestResultDTO<List<TaskDefinitionRespDTO>>> resp =
                taskApi.listByProcessDefinitionCode(1L, 999999999L).execute();

        // 断言远端服务器正常响应
        Assertions.assertThat(resp.isSuccessful()).isTrue();
        Assertions.assertThat(resp.body()).isNotNull();
        Assertions.assertThat(resp.body().getCode()).isEqualTo(0);
        Assertions.assertThat(resp.body().getMsg()).containsIgnoringCase("success");
    }

    /**
     * 本地模拟测试
     */
    private void testWithMockServer() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            String json = "{\n" +
                    "  \"code\": 0,\n" +
                    "  \"msg\": \"success\",\n" +
                    "  \"data\": [\n" +
                    "    {\"code\": 70001, \"version\": 1, \"name\": \"TD-Test-1\", \"description\": \"desc-A\", \"taskType\": \"SHELL\"},\n" +
                    "    {\"code\": 70002, \"version\": 2, \"name\": \"TD-Test-2\", \"description\": \"desc-B\", \"taskType\": \"SQL\"}\n" +
                    "  ]\n" +
                    "}";
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(json));
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
                TaskApi api = context.getBean(TaskApi.class);
                try {
                    Response<HttpRestResultDTO<List<TaskDefinitionRespDTO>>> resp =
                            api.listByProcessDefinitionCode(321L, 654321L).execute();
                    Assertions.assertThat(resp.isSuccessful()).isTrue();
                    HttpRestResultDTO<List<TaskDefinitionRespDTO>> wrapper = resp.body();
                    Assertions.assertThat(wrapper.getCode()).isEqualTo(0);
                    Assertions.assertThat(wrapper.getMsg()).isEqualTo("success");
                    Assertions.assertThat(wrapper.getData()).hasSize(2);
                    Assertions.assertThat(wrapper.getData().get(0).getName()).isEqualTo("TD-Test-1");
                    Assertions.assertThat(wrapper.getData().get(1).getTaskType()).isEqualTo("SQL");

                    // 校验请求
                    RecordedRequest req = server.takeRequest();
                    Assertions.assertThat(req.getMethod()).isEqualTo("GET");
                    Assertions.assertThat(req.getPath()).isEqualTo("/projects/321/task-definition/list-by-process-definition-code/654321");
                    Assertions.assertThat(req.getHeader("token")).isEqualTo(token);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}

