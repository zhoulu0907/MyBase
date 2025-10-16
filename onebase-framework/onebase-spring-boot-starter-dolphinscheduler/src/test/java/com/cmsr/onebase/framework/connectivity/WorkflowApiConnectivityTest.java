/**
 * WorkflowApi 连通性测试（基于 MockWebServer）
 *
 * 覆盖点：
 * - Retrofit 装配可用
 * - 请求路径/方法/鉴权头正确
 * - HttpRestResultDTO<PageInfoDTO<ProcessDefineRespDTO>> 映射正确
 * - 使用独特测试数据便于断言
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
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import retrofit2.Response;

public class WorkflowApiConnectivityTest {

    @Test
    void page_should_call_correct_endpoint_and_map_response() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            ApplicationContextRunner runner = new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(DolphinSchedulerClientAutoConfiguration.class))
                    .withPropertyValues("onebase.dolphinscheduler.client.enable-live-connectivity-test=true");

            runner.run(context -> {
                DolphinSchedulerClientProperties properties = context.getBean(DolphinSchedulerClientProperties.class);
                if (!properties.isEnableLiveConnectivityTest()) {
                    return;
                }
                try {
                    // 构造独特响应数据
                    String body = "{\n" +
                            "  \"code\": 0,\n" +
                            "  \"msg\": \"success-UT-workflow-page\",\n" +
                            "  \"data\": {\n" +
                            "    \"total\": 123,\n" +
                            "    \"pageNo\": 3,\n" +
                            "    \"pageSize\": 7,\n" +
                            "    \"records\": [\n" +
                            "      {\n" +
                            "        \"code\": 991122334455,\n" +
                            "        \"name\": \"UT-Workflow-凤梨-916\",\n" +
                            "        \"description\": \"UT-desc-甲乙丙丁\",\n" +
                            "        \"version\": 42\n" +
                            "      }\n" +
                            "    ]\n" +
                            "  }\n" +
                            "}";
                    server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(body));
                    server.start();

                    String base = server.url("/").toString();
                    String token = "ut-token-wf-20251016";

                    ApplicationContextRunner innerRunner = new ApplicationContextRunner()
                            .withConfiguration(AutoConfigurations.of(DolphinSchedulerClientAutoConfiguration.class))
                            .withPropertyValues(
                                    "onebase.dolphinscheduler.client.enabled=true",
                                    "onebase.dolphinscheduler.client.baseUrl=" + base,
                                    "onebase.dolphinscheduler.client.token=" + token
                            );

                    innerRunner.run(contextInner -> {
                        Assertions.assertThat(contextInner).hasNotFailed();
                        WorkflowApi api = contextInner.getBean(WorkflowApi.class);
                        try {
                            Response<HttpRestResultDTO<PageInfoDTO<ProcessDefineRespDTO>>> resp =
                                    api.page(11L, 3, 7, "UT-search-桃子").execute();
                            Assertions.assertThat(resp.isSuccessful()).isTrue();
                            HttpRestResultDTO<PageInfoDTO<ProcessDefineRespDTO>> wrapper = resp.body();
                            Assertions.assertThat(wrapper).isNotNull();
                            Assertions.assertThat(wrapper.getCode()).isEqualTo(0);
                            Assertions.assertThat(wrapper.getMsg()).isEqualTo("success-UT-workflow-page");
                            PageInfoDTO<ProcessDefineRespDTO> page = wrapper.getData();
                            Assertions.assertThat(page.getTotal()).isEqualTo(123);
                            Assertions.assertThat(page.getPageNo()).isEqualTo(3);
                            Assertions.assertThat(page.getPageSize()).isEqualTo(7);
                            Assertions.assertThat(page.getRecords()).hasSize(1);
                            ProcessDefineRespDTO first = page.getRecords().get(0);
                            Assertions.assertThat(first.getCode()).isEqualTo(991122334455L);
                            Assertions.assertThat(first.getName()).isEqualTo("UT-Workflow-凤梨-916");
                            Assertions.assertThat(first.getDescription()).isEqualTo("UT-desc-甲乙丙丁");
                            Assertions.assertThat(first.getVersion()).isEqualTo(42);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                    // 校验请求
                    RecordedRequest req = server.takeRequest();
                    Assertions.assertThat(req.getMethod()).isEqualTo("GET");
                    Assertions.assertThat(req.getPath())
                            .isEqualTo("/projects/11/workflow-definition?pageNo=3&pageSize=7&searchVal=UT-search-%E6%A1%83%E5%AD%90");
                    Assertions.assertThat(req.getHeader("token")).isEqualTo("ut-token-wf-20251016");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
