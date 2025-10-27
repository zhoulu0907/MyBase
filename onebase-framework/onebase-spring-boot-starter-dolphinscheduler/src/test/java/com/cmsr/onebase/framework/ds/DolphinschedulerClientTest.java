package com.cmsr.onebase.framework.ds;

import com.cmsr.onebase.framework.ds.client.DolphinSchedulerClient;
import com.cmsr.onebase.framework.ds.model.task.def.HttpTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class DolphinschedulerClientTest {

    private static final Long TEST_PROJECT_CODE = 154596852705760L;

    private DolphinSchedulerClient client;

    private Long workflowCode = 155747039101312L;

    @BeforeEach
    public void init() {
        client = new DolphinSchedulerClient();
        client.setAddress("http://10.0.104.33:12345/dolphinscheduler");
        client.setToken("85806d2d33502cef3036fb30b320e5f5");
        client.setTenantCode("root");
        client.setEnvironmentCode(154612656767296L);
        client.initClient();
    }

    @Test
    @Order(1)
    public void testCreateWorkflow() {
        HttpTask httpTask = HttpTask.ofUrl("https://www.baidu.com")
                .method(HttpTask.HttpMethod.GET)
                .form("q", "你好");

        Long workflowId = client.createSingletonWorkflow(TEST_PROJECT_CODE, "测试创建", httpTask, null);
        assert workflowId != null;
        this.workflowCode = workflowId;
    }

    @Test
    @Order(99)
    public void testPurgeWorkflow() {
        client.purgeWorkflow(TEST_PROJECT_CODE, workflowCode);
    }
}
