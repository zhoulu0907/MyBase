package com.cmsr.onebase.framework;

import com.cmsr.onebase.framework.config.DolphinSchedulerClientAutoConfiguration;
import com.cmsr.onebase.framework.remote.ScheduleApi;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * 自动装配与基础调用的单元测试
 *
 * @author matianyu
 * @date 2025-10-15
 */
public class DolphinSchedulerClientAutoConfigurationTest {

    @Test
    void baseUrlMustEndWithSlash() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(DolphinSchedulerClientAutoConfiguration.class))
                .withPropertyValues(
                        "onebase.dolphinscheduler.client.enabled=true",
                        "onebase.dolphinscheduler.client.baseUrl=http://localhost:12345/ds", // 无斜杠
                        "onebase.dolphinscheduler.client.token=abc"
                );
        runner.run(context -> Assertions.assertThat(context).hasFailed());
    }

    @Test
    void tokenHeaderAndPathShouldBeCorrect() throws Exception {
        MockWebServer server = new MockWebServer();
        try {
            server.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .addHeader("Content-Type", "application/json")
                    .setBody("{\"code\":0,\"msg\":\"success\",\"data\":[]}"));
            server.start();

            String base = server.url("/").toString();
            String token = "unit-test-token";

            ApplicationContextRunner runner = new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(DolphinSchedulerClientAutoConfiguration.class))
                    .withPropertyValues(
                            "onebase.dolphinscheduler.client.enabled=true",
                            "onebase.dolphinscheduler.client.baseUrl=" + base,
                            "onebase.dolphinscheduler.client.token=" + token,
                            "onebase.dolphinscheduler.client.headerName=token"
                    );

            runner.run(context -> {
                Assertions.assertThat(context).hasNotFailed();
                ScheduleApi scheduleApi = context.getBean(ScheduleApi.class);
                try {
                    scheduleApi.list(1L).execute();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                try {
                    var recorded = server.takeRequest();
                    Assertions.assertThat(recorded.getHeader("token")).isEqualTo(token);
                    Assertions.assertThat(recorded.getPath()).isEqualTo("/projects/1/schedules");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        } finally {
            server.shutdown();
        }
    }
}

