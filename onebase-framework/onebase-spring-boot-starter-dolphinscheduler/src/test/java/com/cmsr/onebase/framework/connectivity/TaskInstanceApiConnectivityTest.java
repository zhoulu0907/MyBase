/**
 * TaskInstanceApi 连通性测试（基于 MockWebServer）
 *
 * 覆盖点：
 * - GET /task-instance 分页
 * - GET /task-instances 分页
 * - GET /log/{projectCode}/detail 查询日志
 * - 校验 token Header、请求路径与返回 DTO 映射
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
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import retrofit2.Response;

public class TaskInstanceApiConnectivityTest {

    @Test
    void list_page_queryLog_should_work() throws Exception {
        MockWebServer server = new MockWebServer();
        try {
            // 1) /task-instance
            String listJson = "{\n" +
                    "  \"code\": 0,\n" +
                    "  \"msg\": \"success-UT-taskins-list-桂圆\",\n" +
                    "  \"data\": {\n" +
                    "    \"total\": 3,\n" +
                    "    \"pageNo\": 2,\n" +
                    "    \"pageSize\": 5,\n" +
                    "    \"records\": [ { \"id\": 4001, \"processInstanceId\": 5001, \"name\": \"TI-独特-龙井\", \"state\": \"RUNNING\" } ]\n" +
                    "  }\n" +
                    "}";
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(listJson));
            // 2) /task-instances
            String pageJson = "{\n" +
                    "  \"code\": 0,\n" +
                    "  \"msg\": \"success-UT-taskins-page-普洱\",\n" +
                    "  \"data\": {\n" +
                    "    \"total\": 1,\n" +
                    "    \"pageNo\": 8,\n" +
                    "    \"pageSize\": 2,\n" +
                    "    \"records\": [ { \"id\": 4002, \"processInstanceId\": 5002, \"name\": \"TI-独特-正山\", \"state\": \"SUCCESS\" } ]\n" +
                    "  }\n" +
                    "}";
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(pageJson));
            // 3) /log
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json")
                    .setBody("{\"code\":0,\"msg\":\"success-UT-log-铁观音\",\"data\":{\"log\":\"hello\"}}"));

            server.start();

            String base = server.url("/").toString();
            String token = "ut-token-taskins-20251016";

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
                    Assertions.assertThat(listResp.body().getMsg()).isEqualTo("success-UT-taskins-list-桂圆");
                    Assertions.assertThat(listResp.body().getData()).isNotNull();
                    Assertions.assertThat(listResp.body().getData().getTotal()).isEqualTo(3);

                    Response<HttpRestResultDTO<PageInfoDTO<TaskInstanceQueryRespDTO>>> pageResp =
                            api.page(222L, 8, 2, 654321L).execute();
                    Assertions.assertThat(pageResp.isSuccessful()).isTrue();
                    Assertions.assertThat(pageResp.body()).isNotNull();
                    Assertions.assertThat(pageResp.body().getMsg()).isEqualTo("success-UT-taskins-page-普洱");
                    Assertions.assertThat(pageResp.body().getData()).isNotNull();
                    Assertions.assertThat(pageResp.body().getData().getRecords().get(0).getName()).isEqualTo("TI-独特-正山");

                    Response<HttpRestResultDTO<Object>> logResp = api.queryLog(222L, 4002L, 0, 1024).execute();
                    Assertions.assertThat(logResp.isSuccessful()).isTrue();
                    Assertions.assertThat(logResp.body()).isNotNull();
                    Assertions.assertThat(logResp.body().getMsg()).isEqualTo("success-UT-log-铁观音");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            RecordedRequest r1 = server.takeRequest();
            Assertions.assertThat(r1.getMethod()).isEqualTo("GET");
            Assertions.assertThat(r1.getPath()).isEqualTo("/projects/222/task-instance?pageNo=2&pageSize=5&processInstanceId=123456");
            Assertions.assertThat(r1.getHeader("token")).isEqualTo("ut-token-taskins-20251016");

            RecordedRequest r2 = server.takeRequest();
            Assertions.assertThat(r2.getMethod()).isEqualTo("GET");
            Assertions.assertThat(r2.getPath()).isEqualTo("/projects/222/task-instances?pageNo=8&pageSize=2&processInstanceId=654321");

            RecordedRequest r3 = server.takeRequest();
            Assertions.assertThat(r3.getMethod()).isEqualTo("GET");
            Assertions.assertThat(r3.getPath()).isEqualTo("/log/222/detail?taskInstanceId=4002&skipLineNum=0&limit=1024");
        } finally {
            server.shutdown();
        }
    }
}
