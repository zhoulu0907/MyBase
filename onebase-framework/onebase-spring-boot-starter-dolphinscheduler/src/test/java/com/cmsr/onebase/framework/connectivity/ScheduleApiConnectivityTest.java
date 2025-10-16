/**
 * ScheduleApi 连通性测试（基于 MockWebServer）
 *
 * 覆盖点：
 * - 创建调度(JSON)
 * - 查询调度分页
 * - 调度上线/下线
 * - 删除调度
 * - 校验 token Header、路径与 DTO 映射
 *
 * @author matianyu
 * @date 2025-10-16
 */
package com.cmsr.onebase.framework.connectivity;

import com.cmsr.onebase.framework.config.DolphinSchedulerClientAutoConfiguration;
import com.cmsr.onebase.framework.remote.ScheduleApi;
import com.cmsr.onebase.framework.remote.dto.HttpRestResultDTO;
import com.cmsr.onebase.framework.remote.dto.PageInfoDTO;
import com.cmsr.onebase.framework.remote.dto.schedule.ScheduleDefineParamDTO;
import com.cmsr.onebase.framework.remote.dto.schedule.ScheduleDTO;
import com.cmsr.onebase.framework.remote.dto.schedule.ScheduleInfoRespDTO;
import com.cmsr.onebase.framework.remote.enums.FailureStrategyEnum;
import com.cmsr.onebase.framework.remote.enums.PriorityEnum;
import com.cmsr.onebase.framework.remote.enums.WarningTypeEnum;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import retrofit2.Response;

public class ScheduleApiConnectivityTest {

    @Test
    void create_list_online_offline_delete_should_work() throws Exception {
        MockWebServer server = new MockWebServer();
        try {
            // 1) create
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json")
                    .setBody("{\"code\":0,\"msg\":\"success-UT-schedule-create-荔枝\",\"data\":{\"id\":101,\"workflowDefinitionName\":\"WF-独特-醪糟\"}}"));
            // 2) list
            String listJson = "{\n" +
                    "  \"code\": 0,\n" +
                    "  \"msg\": \"success-UT-schedule-list-甘蔗\",\n" +
                    "  \"data\": {\n" +
                    "    \"total\": 1,\n" +
                    "    \"pageNo\": 1,\n" +
                    "    \"pageSize\": 10,\n" +
                    "    \"records\": [ { \"id\": 101, \"crontab\": \"0 0 * * * ?\" } ]\n" +
                    "  }\n" +
                    "}";
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(listJson));
            // 3) online
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json")
                    .setBody("{\"code\":0,\"msg\":\"success-UT-online-橄榄\",\"data\":\"ok\"}"));
            // 4) offline
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json")
                    .setBody("{\"code\":0,\"msg\":\"success-UT-offline-海棠\",\"data\":\"ok\"}"));
            // 5) delete
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json")
                    .setBody("{\"code\":0,\"msg\":\"success-UT-delete-橘柚\",\"data\":\"ok\"}"));

            server.start();

            String base = server.url("/").toString();
            String token = "ut-token-schedule-20251016";

            ApplicationContextRunner runner = new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(DolphinSchedulerClientAutoConfiguration.class))
                    .withPropertyValues(
                            "onebase.dolphinscheduler.client.enabled=true",
                            "onebase.dolphinscheduler.client.baseUrl=" + base,
                            "onebase.dolphinscheduler.client.token=" + token
                    );

            runner.run(context -> {
                Assertions.assertThat(context).hasNotFailed();
                ScheduleApi api = context.getBean(ScheduleApi.class);
                try {
                    // create
                    ScheduleDTO schedule = new ScheduleDTO();
                    schedule.setStartTime("2025-10-16 00:00:00");
                    schedule.setEndTime("2025-12-31 23:59:59");
                    schedule.setCrontab("0 0/5 * * * ?");

                    ScheduleDefineParamDTO param = new ScheduleDefineParamDTO();
                    param.setSchedule(schedule);
                    param.setFailureStrategy(FailureStrategyEnum.END);
                    param.setWarningType(WarningTypeEnum.ALL);
                    param.setProcessInstancePriority(PriorityEnum.LOW);
                    param.setWorkflowDefinitionCode(99887766L);

                    Response<HttpRestResultDTO<ScheduleInfoRespDTO>> createResp = api.create(111L, param).execute();
                    Assertions.assertThat(createResp.isSuccessful()).isTrue();
                    Assertions.assertThat(createResp.body().getMsg()).isEqualTo("success-UT-schedule-create-荔枝");
                    Assertions.assertThat(createResp.body().getData().getWorkflowDefinitionName()).isEqualTo("WF-独特-醪糟");

                    // list
                    Response<HttpRestResultDTO<PageInfoDTO<ScheduleInfoRespDTO>>> listResp =
                            api.getByWorkflowCode(111L, 1, 10, 99887766L).execute();
                    Assertions.assertThat(listResp.isSuccessful()).isTrue();
                    Assertions.assertThat(listResp.body().getData().getTotal()).isEqualTo(1);

                    // online/offline
                    Assertions.assertThat(api.online(111L, 101L).execute().body().getMsg()).isEqualTo("success-UT-online-橄榄");
                    Assertions.assertThat(api.offline(111L, 101L).execute().body().getMsg()).isEqualTo("success-UT-offline-海棠");

                    // delete
                    Assertions.assertThat(api.delete(111L, 101L).execute().body().getMsg()).isEqualTo("success-UT-delete-橘柚");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            // 校验请求
            RecordedRequest r1 = server.takeRequest();
            Assertions.assertThat(r1.getMethod()).isEqualTo("POST");
            Assertions.assertThat(r1.getPath()).isEqualTo("/projects/111/schedules");
            Assertions.assertThat(r1.getHeader("token")).isEqualTo("ut-token-schedule-20251016");

            RecordedRequest r2 = server.takeRequest();
            Assertions.assertThat(r2.getMethod()).isEqualTo("GET");
            Assertions.assertThat(r2.getPath()).isEqualTo("/projects/111/schedules?pageNo=1&pageSize=10&processDefinitionCode=99887766");

            RecordedRequest r3 = server.takeRequest();
            Assertions.assertThat(r3.getMethod()).isEqualTo("POST");
            Assertions.assertThat(r3.getPath()).isEqualTo("/projects/111/schedules/101/online");

            RecordedRequest r4 = server.takeRequest();
            Assertions.assertThat(r4.getMethod()).isEqualTo("POST");
            Assertions.assertThat(r4.getPath()).isEqualTo("/projects/111/schedules/101/offline");

            RecordedRequest r5 = server.takeRequest();
            Assertions.assertThat(r5.getMethod()).isEqualTo("DELETE");
            Assertions.assertThat(r5.getPath()).isEqualTo("/projects/111/schedules/101");
        } finally {
            server.shutdown();
        }
    }
}

