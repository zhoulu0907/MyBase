/**
 * WorkflowApi 连通性测试
 *
 * 覆盖点：
 * - Retrofit 装配可用
 * - 请求路径/方法/鉴权头正确
 * - HttpRestResultDTO<PageInfoDTO<ProcessDefineRespDTO>> 映射正确
 * - 支持真实服务器测试和本地模拟测试
 *
 * @author matianyu
 * @date 2025-10-16
 */
package com.cmsr.onebase.framework.connectivity;

import com.cmsr.onebase.framework.config.DolphinSchedulerClientAutoConfiguration;
import com.cmsr.onebase.framework.remote.WorkflowApi;
import com.cmsr.onebase.framework.remote.dto.HttpRestResultDTO;
import com.cmsr.onebase.framework.remote.dto.PageInfoDTO;
import com.cmsr.onebase.framework.remote.dto.process.ProcessDefineRespDTO;
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
public class WorkflowApiConnectivityTest {

    @Autowired
    private DolphinSchedulerClientProperties properties;

    @Autowired(required = false)
    private WorkflowApi workflowApi;

    @Test
    void testWorkflowApiConnectivity() throws Exception {
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
        Assertions.assertThat(workflowApi).isNotNull();

        // 测试分页查询接口
        Response<HttpRestResultDTO<PageInfoDTO<ProcessDefineRespDTO>>> resp =
                workflowApi.page(1L, 1, 10, null).execute();

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
            // 构造响应数据
            String body = "{\n" +
                    "  \"code\": 0,\n" +
                    "  \"msg\": \"success\",\n" +
                    "  \"data\": {\n" +
                    "    \"total\": 123,\n" +
                    "    \"pageNo\": 3,\n" +
                    "    \"pageSize\": 7,\n" +
                    "    \"records\": [\n" +
                    "      {\n" +
                    "        \"code\": 991122334455,\n" +
                    "        \"name\": \"UT-Workflow-Test\",\n" +
                    "        \"description\": \"UT-desc-Test\",\n" +
                    "        \"version\": 42\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}";
            server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(body));
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
                WorkflowApi api = context.getBean(WorkflowApi.class);
                try {
                    Response<HttpRestResultDTO<PageInfoDTO<ProcessDefineRespDTO>>> resp =
                            api.page(11L, 3, 7, "UT-search-Test").execute();
                    Assertions.assertThat(resp.isSuccessful()).isTrue();
                    HttpRestResultDTO<PageInfoDTO<ProcessDefineRespDTO>> wrapper = resp.body();
                    Assertions.assertThat(wrapper).isNotNull();
                    Assertions.assertThat(wrapper.getCode()).isEqualTo(0);
                    Assertions.assertThat(wrapper.getMsg()).isEqualTo("success");
                    PageInfoDTO<ProcessDefineRespDTO> page = wrapper.getData();
                    Assertions.assertThat(page.getTotal()).isEqualTo(123);
                    Assertions.assertThat(page.getPageNo()).isEqualTo(3);
                    Assertions.assertThat(page.getPageSize()).isEqualTo(7);
                    Assertions.assertThat(page.getRecords()).hasSize(1);
                    ProcessDefineRespDTO first = page.getRecords().get(0);
                    Assertions.assertThat(first.getCode()).isEqualTo(991122334455L);
                    Assertions.assertThat(first.getName()).isEqualTo("UT-Workflow-Test");
                    Assertions.assertThat(first.getDescription()).isEqualTo("UT-desc-Test");
                    Assertions.assertThat(first.getVersion()).isEqualTo(42);

                    // 校验请求
                    RecordedRequest req = server.takeRequest();
                    Assertions.assertThat(req.getMethod()).isEqualTo("GET");
                    Assertions.assertThat(req.getPath())
                            .contains("/projects/11/workflow-definition")
                            .contains("pageNo=3")
                            .contains("pageSize=7")
                            .contains("searchVal=UT-search-Test");
                    Assertions.assertThat(req.getHeader("token")).isEqualTo(token);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
