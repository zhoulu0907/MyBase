package com.cmsr.onebase.framework.connectivity;

import com.cmsr.onebase.framework.config.DolphinSchedulerClientAutoConfiguration;
import com.cmsr.onebase.framework.remote.dto.HttpRestResultDTO;
import jakarta.annotation.Resource;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import retrofit2.Response;
import retrofit2.Retrofit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 使用示例配置发起一次真实调用，验证与 DolphinScheduler 的连通性。
 *
 * 启用方式：在 src/test/resources/application.yml 设置：
 * onebase.dolphinscheduler.client.enable-live-connectivity-test: true
 *
 * 注意：该测试将读取 src/test/resources/application.yml 中的 baseUrl 与 token。
 */
@SpringBootTest(classes = DolphinSchedulerClientAutoConfiguration.class)
public class DolphinSchedulerConnectivityIT {
    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerConnectivityIT.class);

    @Resource
    private Retrofit retrofit;

    @Resource
    private OkHttpClient okHttpClient;

    @Value("${onebase.dolphinscheduler.client.enable-live-connectivity-test:false}")
    private boolean enableLiveConnectivityTest;

    @Test
    void testLiveConnectivity() throws Exception {
        // 未开启时跳过真实联通性测试
        Assumptions.assumeTrue(enableLiveConnectivityTest, "未开启 live 连通性测试，跳过，该开关见 onebase.dolphinscheduler.client.enable-live-connectivity-test");

        HealthApi api = retrofit.create(HealthApi.class);
        Response<HttpRestResultDTO<Object>> resp = api.getCurrentUser().execute();
        assertTrue(resp.isSuccessful(), "HTTP 请求失败，code=" + resp.code());
        HttpRestResultDTO<Object> body = resp.body();
        assertNotNull(body, "响应体为空");
        // DolphinScheduler 通用成功码通常为 0
        assertEquals(0, body.getCode(), "业务响应码非成功，msg=" + body.getMsg());
        logger.info("连通性测试通过，当前用户信息：{}", body.getData());
    }

    @Test
    void testBeanConfiguration() {
        // 若已开启 live 测试，则此处跳过；否则校验 Bean 注入
        Assumptions.assumeFalse(enableLiveConnectivityTest, "已开启 live 连通性测试，跳过 Bean 装配校验");

        assertNotNull(retrofit);
        assertNotNull(okHttpClient);
    }
}
