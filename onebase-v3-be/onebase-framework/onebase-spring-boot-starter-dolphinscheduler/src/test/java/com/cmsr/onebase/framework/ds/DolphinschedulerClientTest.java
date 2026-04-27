package com.cmsr.onebase.framework.ds;

import com.cmsr.onebase.framework.ds.client.DolphinSchedulerClient;
import com.cmsr.onebase.framework.ds.model.schedule.sub.Schedule;
import com.cmsr.onebase.framework.ds.model.task.def.HttpTask;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DolphinschedulerClientTest {

    private static final Long TEST_PROJECT_CODE = 154596852705760L;

    private DolphinSchedulerClient client;

    private Long workflowCode = 155765171524992L;

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

        Long workflowId = client.createSingletonHttpWorkflow(TEST_PROJECT_CODE, "测试创建", httpTask, null);
        assert workflowId != null;
        this.workflowCode = workflowId;
    }

    @Test
    @Order(0)
    public void testPageQuery() {
        String flowName = "测试";
        List<Long> workflowList = client.queryWorkflowCodeListByName(TEST_PROJECT_CODE, flowName);

        assert CollectionUtils.isNotEmpty(workflowList);
    }

    @Test
    @Order(2)
    public void testOnlineWorkflow() {
        Schedule schedule = new Schedule();
        schedule.setStartTime(LocalDateTime.now());
        schedule.setEndTime(LocalDateTime.now().withYear(2125));
        schedule.setTimezoneId("Asia/Shanghai");
        schedule.setCrontab("0 0 1 * * ? 2030");

        client.onlineWorkflowWithSchedule(TEST_PROJECT_CODE, workflowCode, schedule);
    }

    @Test
    @Order(3)
    public void testPurgeWorkflow() {
        client.purgeWorkflow(TEST_PROJECT_CODE, workflowCode);
    }
}
