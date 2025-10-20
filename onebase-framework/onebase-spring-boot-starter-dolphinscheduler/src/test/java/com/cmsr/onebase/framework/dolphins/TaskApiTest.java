package com.cmsr.onebase.framework.dolphins;

import com.cmsr.onebase.framework.dolphins.api.TaskApi;
import com.cmsr.onebase.framework.dolphins.config.DolphinSchedulerProperties;
import com.cmsr.onebase.framework.dolphins.dto.task.model.TaskDefinitionDTO;
import com.cmsr.onebase.framework.dolphins.dto.task.request.TaskQueryRequestDTO;
import com.cmsr.onebase.framework.dolphins.dto.task.response.TaskGetResponseDTO;
import com.cmsr.onebase.framework.dolphins.dto.task.response.TaskPageResponseDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import retrofit2.Response;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;

/**
 * TaskApi 真实环境集成测试
 *
 * 说明：swagger 暴露的 Task 相关接口仅包含查询与详情（不含创建/更新/删除），
 * 因此本测试覆盖：分页查询与详情查询；字段校验覆盖 data 结构。
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskApiTest {

    @Resource
    private TaskApi taskApi;

    @Resource
    private DolphinSchedulerProperties dsProps;

    private Long firstTaskCode;

    @BeforeAll
    void checkConnectivityOrSkip() {
        try {
            URI uri = URI.create(dsProps.getBaseUrl());
            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : ("https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80);
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 3000);
            }
            log.info("DolphinScheduler 可达: {}:{}", host, port);
        } catch (Exception ex) {
            Assumptions.assumeTrue(false, "网络不可达，跳过 TaskApi 集成测试: " + ex.getMessage());
        }
    }

    @Test
    @Order(1)
    public void test_01_filterTaskDefinition() throws Exception {
        TaskQueryRequestDTO req = new TaskQueryRequestDTO();
        req.setPageNo(1);
        req.setPageSize(10);
        // 可选条件：项目名、任务名、任务类型
        // req.setProjectName("default");
        // req.setName("my-task");
        // req.setTaskType("SHELL");

        Response<TaskPageResponseDTO> resp = taskApi.filterTaskDefinition(req).execute();
        Assertions.assertTrue(resp.isSuccessful());
        TaskPageResponseDTO body = resp.body();
        Assertions.assertNotNull(body);
        assertSuccess(body.getCode(), body.getSuccess(), body.getMsg());

        if (body.getData() != null && body.getData().getTotalList() != null && !body.getData().getTotalList().isEmpty()) {
            TaskDefinitionDTO first = body.getData().getTotalList().get(0);
            Assertions.assertNotNull(first.getCode(), "任务 code 不应为空");
            Assertions.assertNotNull(first.getName(), "任务 name 不应为空");
            // 记录一个 taskCode 供详情接口测试
            firstTaskCode = first.getCode();
        }
    }

    @Test
    @Order(2)
    public void test_02_getTaskDefinition() throws Exception {
        Assumptions.assumeTrue(firstTaskCode != null, "无可用任务用于详情查询，可能分页数据为空，跳过");
        Response<TaskGetResponseDTO> resp = taskApi.getTaskDefinition(firstTaskCode).execute();
        Assertions.assertTrue(resp.isSuccessful());
        TaskGetResponseDTO body = resp.body();
        Assertions.assertNotNull(body);
        assertSuccess(body.getCode(), body.getSuccess(), body.getMsg());

        TaskDefinitionDTO data = body.getData();
        Assertions.assertNotNull(data);
        Assertions.assertEquals(firstTaskCode, data.getCode());
        Assertions.assertNotNull(data.getTaskType());
    }

    private static void assertSuccess(Integer code, Boolean success, String msg) {
        boolean ok = (code != null && code == 0)
                || Boolean.TRUE.equals(success)
                || (msg != null && msg.toLowerCase().contains("success"));
        Assertions.assertTrue(ok, () -> "接口返回失败: code=" + code + ", success=" + success + ", msg=" + msg);
    }
}
