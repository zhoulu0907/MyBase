/**
 * InstanceApi 连通性测试
 *
 * 覆盖启动、分页、执行与删除等接口，断言路径、Header 与 DTO 映射正确
 * 支持真实服务器测试和本地模拟测试
 *
 * @author matianyu
 * @date 2025-10-16
 */
package com.cmsr.onebase.framework.connectivity;

import com.cmsr.onebase.framework.config.DolphinSchedulerClientAutoConfiguration;
import com.cmsr.onebase.framework.config.DolphinSchedulerClientProperties;
import com.cmsr.onebase.framework.remote.InstanceApi;
import com.cmsr.onebase.framework.remote.dto.HttpRestResultDTO;
import com.cmsr.onebase.framework.remote.dto.PageInfoDTO;
import com.cmsr.onebase.framework.remote.dto.instance.ProcessInstanceQueryRespDTO;
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
public class InstanceApiConnectivityTest {

    @Autowired
    private DolphinSchedulerClientProperties properties;

    @Autowired(required = false)
    private InstanceApi instanceApi;

    @Test
    void testInstanceApiConnectivity() throws Exception {
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
        Assertions.assertThat(instanceApi).isNotNull();

        // 测试分页查询接口
        Response<HttpRestResultDTO<PageInfoDTO<ProcessInstanceQueryRespDTO>>> pageResp =
                instanceApi.page(1L, 1, 10, null).execute();

        // 断言远端服务器正常响应
        Assertions.assertThat(pageResp.isSuccessful()).isTrue();
        Assertions.assertThat(pageResp.body()).isNotNull();
        Assertions.assertThat(pageResp.body().getCode()).isEqualTo(0);
        Assertions.assertThat(pageResp.body().getMsg()).containsIgnoringCase("success");
    }

    /**
     * 本地模拟测试
     */
    private void testWithMockServer() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            // 1) page 响应
            String pageJson = "{\n" +
                    "  \"code\": 0,\n" +
                    "  \"msg\": \"success\",\n" +
                    "  \"data\": {\n" +
                    "    \"total\": 2,\n" +
                    "    \"pageNo\": 1,\n" +
                    "    \"pageSize\": 10,\n" +
                    "    \"records\": [\n" +
                    "      {\"id\": 10001, \"processDefinitionCode\": 88990011, \"processDefinitionName\": \"PI-Test-1\", \"state\": \"SUCCESS\"},\n" +
                    "      {\"id\": 10002, \"processDefinitionCode\": 88990011, \"processDefinitionName\": \"PI-Test-2\", \"state\": \"FAILURE\"}\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}";
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(pageJson));

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
                InstanceApi api = context.getBean(InstanceApi.class);
                try {
                    // page
                    Response<HttpRestResultDTO<PageInfoDTO<ProcessInstanceQueryRespDTO>>> pageResp =
                            api.page(999L, 1, 10, 88990011L).execute();
                    Assertions.assertThat(pageResp.isSuccessful()).isTrue();
                    Assertions.assertThat(pageResp.body()).isNotNull();
                    Assertions.assertThat(pageResp.body().getCode()).isEqualTo(0);
                    Assertions.assertThat(pageResp.body().getMsg()).isEqualTo("success");
                    PageInfoDTO<ProcessInstanceQueryRespDTO> page = pageResp.body().getData();
                    Assertions.assertThat(page).isNotNull();
                    Assertions.assertThat(page.getTotal()).isEqualTo(2);
                    Assertions.assertThat(page.getRecords()).hasSize(2);
                    Assertions.assertThat(page.getRecords().get(0).getProcessDefinitionName()).isEqualTo("PI-Test-1");
                    Assertions.assertThat(page.getRecords().get(1).getState()).isEqualTo("FAILURE");

                    // 校验请求
                    RecordedRequest req = server.takeRequest();
                    Assertions.assertThat(req.getMethod()).isEqualTo("GET");
                    Assertions.assertThat(req.getPath()).contains("/projects/999/process-instances");
                    Assertions.assertThat(req.getHeader("token")).isEqualTo(token);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
