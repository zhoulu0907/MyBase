/**
 * ScheduleApi 连通性测试
 *
 * 覆盖点：
 * - 创建调度(JSON)
 * - 查询调度分页
 * - 调度上线/下线
 * - 删除调度
 * - 校验 token Header、路径与 DTO 映射
 * - 支持真实服务器测试和本地模拟测试
 *
 * @author matianyu
 * @date 2025-10-16
 */
package com.cmsr.onebase.framework.connectivity;

import com.cmsr.onebase.framework.config.DolphinSchedulerClientAutoConfiguration;
import com.cmsr.onebase.framework.config.DolphinSchedulerClientProperties;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import retrofit2.Response;

@SpringBootTest
public class ScheduleApiConnectivityTest {

    @Autowired
    private DolphinSchedulerClientProperties properties;

    @Autowired(required = false)
    private ScheduleApi scheduleApi;

    @Test
    void testScheduleApiConnectivity() throws Exception {
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
        Assertions.assertThat(scheduleApi).isNotNull();

        System.out.println("=== 真实服务器测试开始 ===");
        System.out.println("DolphinScheduler URL: " + properties.getBaseUrl());
        System.out.println("Token: " + properties.getToken().substring(0, 8) + "...");

        Response<HttpRestResultDTO<PageInfoDTO<ScheduleInfoRespDTO>>> listResp = null;
        try {
            // 测试查询调度分页接口
            listResp = scheduleApi.getByWorkflowCode(1L, 1, 10, null).execute();
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
        System.out.println("HTTP状态码: " + listResp.code());
        System.out.println("是否成功: " + listResp.isSuccessful());
        
        if (!listResp.isSuccessful()) {
            System.out.println("\n✗ HTTP请求失败!");
            System.out.println("错误响应体:");
            if (listResp.errorBody() != null) {
                String errorContent = listResp.errorBody().string();
                System.out.println(errorContent.substring(0, Math.min(500, errorContent.length())));
            }
            Assertions.fail("HTTP请求失败,状态码: " + listResp.code());
        }

        // 如果响应体为null,打印原始响应
        if (listResp.body() == null) {
            System.out.println("\n⚠️ 警告: 响应体为null,服务器可能返回了非JSON内容");
            System.out.println("响应头:");
            listResp.headers().forEach(header -> 
                System.out.println("  " + header.getFirst() + ": " + header.getSecond())
            );
            System.out.println("\n可能原因:");
            System.out.println("  1. Token认证失败,返回了HTML登录页面");
            System.out.println("  2. API版本不匹配");
            System.out.println("请参考TROUBLESHOOTING.md文档进行问题排查");
            Assertions.fail("响应体为null,服务器返回了非JSON内容(如HTML登录页面)");
        }

        // 断言远端服务器正常响应
        Assertions.assertThat(listResp.body()).isNotNull();
        System.out.println("响应code: " + listResp.body().getCode());
        System.out.println("响应msg: " + listResp.body().getMsg());
        
        Assertions.assertThat(listResp.body().getCode()).isEqualTo(0);
        Assertions.assertThat(listResp.body().getMsg()).containsIgnoringCase("success");
        
        System.out.println("\n✓ 真实服务器测试通过!");
    }

    /**
     * 本地模拟测试
     */
    private void testWithMockServer() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            // 1) create
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json")
                    .setBody("{\"code\":0,\"msg\":\"success\",\"data\":{\"id\":101,\"workflowDefinitionName\":\"WF-Test\"}}"));
            // 2) list
            String listJson = "{\n" +
                    "  \"code\": 0,\n" +
                    "  \"msg\": \"success\",\n" +
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
                    .setBody("{\"code\":0,\"msg\":\"success\",\"data\":\"ok\"}"));
            // 4) offline
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json")
                    .setBody("{\"code\":0,\"msg\":\"success\",\"data\":\"ok\"}"));
            // 5) delete
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json")
                    .setBody("{\"code\":0,\"msg\":\"success\",\"data\":\"ok\"}"));

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
                    Assertions.assertThat(createResp.body().getCode()).isEqualTo(0);
                    Assertions.assertThat(createResp.body().getMsg()).isEqualTo("success");

                    // list
                    Response<HttpRestResultDTO<PageInfoDTO<ScheduleInfoRespDTO>>> listResp =
                            api.getByWorkflowCode(111L, 1, 10, 99887766L).execute();
                    Assertions.assertThat(listResp.isSuccessful()).isTrue();
                    Assertions.assertThat(listResp.body().getCode()).isEqualTo(0);
                    Assertions.assertThat(listResp.body().getData().getTotal()).isEqualTo(1);

                    // online/offline
                    Response<HttpRestResultDTO<String>> onlineResp = api.online(111L, 101L).execute();
                    Assertions.assertThat(onlineResp.body().getCode()).isEqualTo(0);
                    Assertions.assertThat(onlineResp.body().getMsg()).isEqualTo("success");

                    Response<HttpRestResultDTO<String>> offlineResp = api.offline(111L, 101L).execute();
                    Assertions.assertThat(offlineResp.body().getCode()).isEqualTo(0);
                    Assertions.assertThat(offlineResp.body().getMsg()).isEqualTo("success");

                    // delete
                    Response<HttpRestResultDTO<String>> deleteResp = api.delete(111L, 101L).execute();
                    Assertions.assertThat(deleteResp.body().getCode()).isEqualTo(0);
                    Assertions.assertThat(deleteResp.body().getMsg()).isEqualTo("success");

                    // 校验请求
                    RecordedRequest r1 = server.takeRequest();
                    Assertions.assertThat(r1.getMethod()).isEqualTo("POST");
                    Assertions.assertThat(r1.getPath()).isEqualTo("/projects/111/schedules");
                    Assertions.assertThat(r1.getHeader("token")).isEqualTo(token);

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
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}

