package com.cmsr.onebase.framework.connectivity;

import com.cmsr.onebase.framework.config.DolphinSchedulerClientAutoConfiguration;
import com.cmsr.onebase.framework.config.DolphinSchedulerClientProperties;
import com.cmsr.onebase.framework.remote.WorkflowApi;
import com.cmsr.onebase.framework.remote.dto.HttpRestResultDTO;
import com.cmsr.onebase.framework.remote.dto.PageInfoDTO;
import com.cmsr.onebase.framework.remote.dto.process.ProcessDefineRespDTO;
import jakarta.annotation.Resource;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerConnectivityIT.class);    @Resource
    private Retrofit retrofit;

    @Resource
    private OkHttpClient okHttpClient;    @Resource
    private DolphinSchedulerClientProperties properties;    private final ConnectivityTestHelper connectivityTestHelper = new ConnectivityTestHelper();@Test
    void testLiveConnectivity() throws Exception {
        // 未开启时跳过真实联通性测试
        Assumptions.assumeTrue(properties.isEnableLiveConnectivityTest(), 
            "未开启 live 连通性测试，跳过，该开关见 onebase.dolphinscheduler.client.enable-live-connectivity-test");

        // 使用辅助工具进行连通性测试
        boolean success = connectivityTestHelper.executeConnectivityTest(properties, () -> {
            HealthApi api = retrofit.create(HealthApi.class);
            Response<HttpRestResultDTO<Object>> resp = api.getCurrentUser().execute();
            assertTrue(resp.isSuccessful(), "HTTP 请求失败，code=" + resp.code());
            HttpRestResultDTO<Object> body = resp.body();
            assertNotNull(body, "响应体为空");
            // DolphinScheduler 通用成功码通常为 0
            assertEquals(0, body.getCode(), "业务响应码非成功，msg=" + body.getMsg());
            logger.info("连通性测试通过，当前用户信息：{}", body.getData());
        }, "用户信息获取");
        
        assertTrue(success, "连通性测试未成功完成");
    }    @Test
    void testWorkflowApiConnectivity() throws Exception {
        // 未开启时跳过真实联通性测试
        Assumptions.assumeTrue(properties.isEnableLiveConnectivityTest(), 
            "未开启 live 连通性测试，跳过，该开关见 onebase.dolphinscheduler.client.enable-live-connectivity-test");

        // 测试工作流 API
        boolean success = connectivityTestHelper.executeConnectivityTest(properties, () -> {
            WorkflowApi api = retrofit.create(WorkflowApi.class);
            Response<HttpRestResultDTO<PageInfoDTO<ProcessDefineRespDTO>>> resp = 
                api.page(1L, 1, 10, null).execute();
            assertTrue(resp.isSuccessful(), "工作流API请求失败，code=" + resp.code());
            logger.info("工作流API连通性测试通过");
        }, "工作流定义查询");
        
        assertTrue(success, "工作流API连通性测试未成功完成");
    }@Test
    void testBeanConfiguration() {
        // 若已开启 live 测试，则此处跳过；否则校验 Bean 注入
        Assumptions.assumeFalse(properties.isEnableLiveConnectivityTest(), "已开启 live 连通性测试，跳过 Bean 装配校验");

        assertNotNull(retrofit);
        assertNotNull(okHttpClient);
        assertNotNull(properties);
    }    @Test
    void testVirtualConnectivity() {
        // 未开启时进行虚拟连通性测试
        Assumptions.assumeFalse(properties.isEnableLiveConnectivityTest(), "已开启 live 连通性测试，跳过虚拟测试");

        // 使用辅助工具进行虚拟测试
        connectivityTestHelper.executeVirtualTest(properties, "完整功能虚拟测试");
        
        // 验证 Bean 配置正确
        assertNotNull(retrofit);
        assertNotNull(okHttpClient);
        assertNotNull(properties);
        
        // 验证 API 接口可以创建
        HealthApi healthApi = retrofit.create(HealthApi.class);
        assertNotNull(healthApi);
        
        WorkflowApi workflowApi = retrofit.create(WorkflowApi.class);
        assertNotNull(workflowApi);
        
        logger.info("虚拟连通性测试通过，所有 Bean 和 API 配置正确");
    }
}
