/**
 * InstanceApi 连通性测试（基于 MockWebServer）
 *
 * 覆盖启动、分页、执行与删除等接口，断言路径、Header 与 DTO 映射正确。
 *
 * @author matianyu
 * @date 2025-10-16
 */
package com.cmsr.onebase.framework.connectivity;

import com.cmsr.onebase.framework.config.DolphinSchedulerClientAutoConfiguration;
import com.cmsr.onebase.framework.remote.InstanceApi;
import com.cmsr.onebase.framework.remote.dto.HttpRestResultDTO;
import com.cmsr.onebase.framework.remote.dto.PageInfoDTO;
import com.cmsr.onebase.framework.remote.dto.instance.ProcessInstanceCreateParamDTO;
import com.cmsr.onebase.framework.remote.dto.instance.ProcessInstanceQueryRespDTO;
import com.cmsr.onebase.framework.remote.dto.instance.ProcessInstanceRunParamDTO;
import com.cmsr.onebase.framework.remote.enums.ExecuteTypeEnum;
import com.cmsr.onebase.framework.remote.enums.FailureStrategyEnum;
import com.cmsr.onebase.framework.remote.enums.PriorityEnum;
import com.cmsr.onebase.framework.remote.enums.TaskDependTypeEnum;
import com.cmsr.onebase.framework.remote.enums.WarningTypeEnum;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import retrofit2.Response;

public class InstanceApiConnectivityTest {

    @Test
    void start_and_page_and_execute_and_delete_should_work() throws Exception {
        MockWebServer server = new MockWebServer();
        try {
            // 1) start 响应
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json")
                    .setBody("{\"code\":0,\"msg\":\"success-UT-start-菠萝\",\"data\":{}}"));
            // 2) page 响应（仅使用 DTO 定义的字段）
            String pageJson = "{\n" +
                    "  \"code\": 0,\n" +
                    "  \"msg\": \"success-UT-page-杨桃\",\n" +
                    "  \"data\": {\n" +
                    "    \"total\": 2,\n" +
                    "    \"pageNo\": 9,\n" +
                    "    \"pageSize\": 4,\n" +
                    "    \"records\": [\n" +
                    "      {\"id\": 10001, \"processDefinitionCode\": 88990011, \"processDefinitionName\": \"PI-独特-红袍\", \"state\": \"SUCCESS\"},\n" +
                    "      {\"id\": 10002, \"processDefinitionCode\": 88990011, \"processDefinitionName\": \"PI-独特-碧螺\", \"state\": \"FAILURE\"}\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}";
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(pageJson));
            // 3) execute 响应
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json")
                    .setBody("{\"code\":0,\"msg\":\"success-UT-execute-榴莲\",\"data\":\"done\"}"));
            // 4) delete 响应
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json")
                    .setBody("{\"code\":0,\"msg\":\"success-UT-delete-山竹\",\"data\":\"ok\"}"));

            server.start();

            String base = server.url("/").toString();
            String token = "ut-token-inst-20251016";

            ApplicationContextRunner runner = new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(DolphinSchedulerClientAutoConfiguration.class))
                    .withPropertyValues(
                            "onebase.dolphinscheduler.client.enabled=true",
                            "onebase.dolphinscheduler.client.baseUrl=" + base,
                            "onebase.dolphinscheduler.client.token=" + token
                    );

            runner.run(context -> {
                Assertions.assertThat(context).hasNotFailed();
                InstanceApi api = context.getBean(InstanceApi.class);
                try {
                    // start
                    ProcessInstanceCreateParamDTO start = new ProcessInstanceCreateParamDTO();
                    start.setFailureStrategy(FailureStrategyEnum.CONTINUE);
                    start.setProcessDefinitionCode(88990011L);
                    start.setProcessInstancePriority(PriorityEnum.HIGH);
                    start.setScheduleTime("2025-10-16 08:00:00");
                    start.setWarningGroupId(556677L);
                    start.setWarningType(WarningTypeEnum.SUCCESS);
                    start.setDryRun(0);
                    start.setEnvironmentCode("env-独特-甲");
                    start.setExecType(ExecuteTypeEnum.RE_RUN);
                    start.setExpectedParallelismNumber("3");
                    start.setRunMode("RUN_MODE-独特");
                    start.setStartNodeList("[1001,1002]");
                    start.setStartParams("{\\\"k\\\":\\\"v-独特\\\"}");
                    start.setTaskDependType(TaskDependTypeEnum.TASK_ONLY);
                    start.setWorkerGroup("group-独特-壹");

                    Response<HttpRestResultDTO<Object>> startResp = api.start(999L, start).execute();
                    Assertions.assertThat(startResp.isSuccessful()).isTrue();
                    Assertions.assertThat(startResp.body()).isNotNull();
                    Assertions.assertThat(startResp.body().getMsg()).isEqualTo("success-UT-start-菠萝");

                    // page
                    Response<HttpRestResultDTO<PageInfoDTO<ProcessInstanceQueryRespDTO>>> pageResp =
                            api.page(999L, 9, 4, 88990011L).execute();
                    Assertions.assertThat(pageResp.isSuccessful()).isTrue();
                    Assertions.assertThat(pageResp.body()).isNotNull();
                    PageInfoDTO<ProcessInstanceQueryRespDTO> page = pageResp.body().getData();
                    Assertions.assertThat(page).isNotNull();
                    Assertions.assertThat(page.getTotal()).isEqualTo(2);
                    Assertions.assertThat(page.getPageNo()).isEqualTo(9);
                    Assertions.assertThat(page.getPageSize()).isEqualTo(4);
                    Assertions.assertThat(page.getRecords()).hasSize(2);
                    Assertions.assertThat(page.getRecords().get(0).getProcessDefinitionName()).isEqualTo("PI-独特-红袍");
                    Assertions.assertThat(page.getRecords().get(1).getState()).isEqualTo("FAILURE");

                    // execute
                    ProcessInstanceRunParamDTO run = new ProcessInstanceRunParamDTO();
                    run.setProcessInstanceId(20001L);
                    run.setExecuteType(ExecuteTypeEnum.PAUSE);
                    Response<HttpRestResultDTO<String>> exeResp = api.execute(999L, run).execute();
                    Assertions.assertThat(exeResp.isSuccessful()).isTrue();
                    Assertions.assertThat(exeResp.body()).isNotNull();
                    Assertions.assertThat(exeResp.body().getMsg()).isEqualTo("success-UT-execute-榴莲");
                    Assertions.assertThat(exeResp.body().getData()).isEqualTo("done");

                    // delete
                    Response<HttpRestResultDTO<String>> delResp = api.delete(999L, 20002L).execute();
                    Assertions.assertThat(delResp.isSuccessful()).isTrue();
                    Assertions.assertThat(delResp.body()).isNotNull();
                    Assertions.assertThat(delResp.body().getMsg()).isEqualTo("success-UT-delete-山竹");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            // 校验请求1: start
            RecordedRequest r1 = server.takeRequest();
            Assertions.assertThat(r1.getMethod()).isEqualTo("POST");
            Assertions.assertThat(r1.getPath()).isEqualTo("/projects/999/executors/start-process-instance");
            Assertions.assertThat(r1.getHeader("token")).isEqualTo("ut-token-inst-20251016");
            // 校验请求2: page
            RecordedRequest r2 = server.takeRequest();
            Assertions.assertThat(r2.getMethod()).isEqualTo("GET");
            Assertions.assertThat(r2.getPath()).isEqualTo("/projects/999/process-instances?pageNo=9&pageSize=4&processDefineCode=88990011");
            // 校验请求3: execute
            RecordedRequest r3 = server.takeRequest();
            Assertions.assertThat(r3.getMethod()).isEqualTo("POST");
            Assertions.assertThat(r3.getPath()).isEqualTo("/projects/999/executors/execute");
            // 校验请求4: delete
            RecordedRequest r4 = server.takeRequest();
            Assertions.assertThat(r4.getMethod()).isEqualTo("DELETE");
            Assertions.assertThat(r4.getPath()).isEqualTo("/projects/999/process-instances/20002");
        } finally {
            server.shutdown();
        }
    }
}
