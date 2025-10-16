/**
 * TaskApi 连通性测试（基于 MockWebServer）
 *
 * 覆盖点：
 * - GET listByProcessDefinitionCode 请求路径与鉴权头
 * - HttpRestResultDTO<List<TaskDefinitionRespDTO>> 映射
 * - 独特测试数据断言
 *
 * @author matianyu
 * @date 2025-10-16
 */
package com.cmsr.onebase.framework.connectivity;

import com.cmsr.onebase.framework.config.DolphinSchedulerClientAutoConfiguration;
import com.cmsr.onebase.framework.remote.TaskApi;
import com.cmsr.onebase.framework.remote.dto.HttpRestResultDTO;
import com.cmsr.onebase.framework.remote.dto.task.TaskDefinitionRespDTO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import retrofit2.Response;

import java.util.List;

public class TaskApiConnectivityTest {

    @Test
    void listByProcessDefinitionCode_should_work() throws Exception {
        MockWebServer server = new MockWebServer();
        try {
            String json = "{\n" +
                    "  \"code\": 0,\n" +
                    "  \"msg\": \"success-UT-task-list-桂花\",\n" +
                    "  \"data\": [\n" +
                    "    {\"code\": 70001, \"version\": 1, \"name\": \"TD-独特-甘草\", \"description\": \"desc-A\", \"taskType\": \"SHELL\"},\n" +
                    "    {\"code\": 70002, \"version\": 2, \"name\": \"TD-独特-薄荷\", \"description\": \"desc-B\", \"taskType\": \"SQL\"}\n" +
                    "  ]\n" +
                    "}";
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(json));
            server.start();

            String base = server.url("/").toString();
            String token = "ut-token-task-20251016";

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
                    Assertions.assertThat(wrapper.getMsg()).isEqualTo("success-UT-task-list-桂花");
                    Assertions.assertThat(wrapper.getData()).hasSize(2);
                    Assertions.assertThat(wrapper.getData().get(0).getName()).isEqualTo("TD-独特-甘草");
                    Assertions.assertThat(wrapper.getData().get(1).getTaskType()).isEqualTo("SQL");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            RecordedRequest req = server.takeRequest();
            Assertions.assertThat(req.getMethod()).isEqualTo("GET");
            Assertions.assertThat(req.getPath()).isEqualTo("/projects/321/task-definition/list-by-process-definition-code/654321");
            Assertions.assertThat(req.getHeader("token")).isEqualTo("ut-token-task-20251016");
        } finally {
            server.shutdown();
        }
    }
}

