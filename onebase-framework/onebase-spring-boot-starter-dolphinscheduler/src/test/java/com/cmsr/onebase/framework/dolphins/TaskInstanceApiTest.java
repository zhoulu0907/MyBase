package com.cmsr.onebase.framework.dolphins;

import com.cmsr.onebase.framework.dolphins.api.TaskInstanceApi;
import com.cmsr.onebase.framework.dolphins.config.DolphinSchedulerProperties;
import com.cmsr.onebase.framework.dolphins.dto.taskinstance.model.TaskInstanceDTO;
import com.cmsr.onebase.framework.dolphins.dto.taskinstance.response.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import retrofit2.Response;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;

/**
 * TaskInstanceApi 真实环境集成测试
 *
 * 测试目标：
 * 1. 验证接口连通性
 * 2. 验证请求体和响应体字段映射与类型（关键字段）
 * 3. 不使用 Mock，真实请求与响应
 * 4. 完整业务流（分页查询→单个查询→操作接口：停止/SavePoint/强制成功）
 *
 * 运行前准备：
 * - 在 src/test/resources/application.yml 配置 onebase.dolphinscheduler.baseUrl 与 token
 * - 需要系统中有已运行或已完成的任务实例数据
 * - 注意：停止、SavePoint、强制成功等操作接口需要谨慎测试，避免影响生产环境
 *
 * 提示：接口返回结构通常包含 code/msg/data，成功 code 多为 0。
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class TaskInstanceApiTest {

    @Resource
    private TaskInstanceApi taskInstanceApi;

    @Resource
    private DolphinSchedulerProperties dsProps;

    private Long testProjectCode;
    private Long testTaskInstanceId;
    private Integer testTaskInstanceIdInt;

    @BeforeAll
    void checkConnectivityOrSkip() {
        // 在受限网络（CI、公司内网）环境下，先做 TCP 直连探测以决定是否跳过全部集成测试
        try {
            URI uri = URI.create(dsProps.getBaseUrl());
            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : ("https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80);
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 3000);
            }
            log.info("DolphinScheduler 可达: {}:{}", host, port);
        } catch (Exception ex) {
            Assumptions.assumeTrue(false, "网络不可达，跳过 TaskInstanceApi 集成测试: " + ex.getMessage());
        }
    }

    @Test
    @Order(1)
    public void test_01_queryTaskListPaging() throws Exception {
        // 使用一个已知的项目Code进行测试，这里需要根据实际环境调整
        // 如果不知道项目Code，可以设置为null，查询所有项目的任务实例
        testProjectCode = 123456789L; // 请根据实际环境修改此值

        Response<TaskInstancePageResponseDTO> resp = taskInstanceApi.queryTaskListPaging(
                testProjectCode,
                null,  // workflowInstanceId
                null,  // workflowInstanceName
                null,  // searchVal
                null,  // taskName
                null,  // taskCode
                null,  // executorName
                null,  // stateType
                null,  // host
                null,  // startDate
                null,  // endDate
                null,  // taskExecuteType
                1,     // pageNo
                10     // pageSize
        ).execute();

        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertTrue(resp.isSuccessful(), "HTTP 状态应成功");
        TaskInstancePageResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");
        assertSuccess(body.getCode(), body.getSuccess(), body.getMsg());

        // 验证分页结构
        Assertions.assertNotNull(body.getData(), "分页 data 不应为空");
        Assertions.assertNotNull(body.getData().getPageNo(), "pageNo 不应为空");
        Assertions.assertNotNull(body.getData().getPageSize(), "pageSize 不应为空");

        // 如果有数据，记录第一个任务实例ID用于后续测试
        if (body.getData().getTotalList() != null && !body.getData().getTotalList().isEmpty()) {
            TaskInstanceDTO first = body.getData().getTotalList().get(0);
            Assertions.assertNotNull(first.getId(), "任务实例 ID 不能为空");
            Assertions.assertNotNull(first.getName(), "任务实例 name 不能为空");
            Assertions.assertNotNull(first.getState(), "任务实例 state 不能为空");
            Assertions.assertNotNull(first.getProjectCode(), "任务实例 projectCode 不能为空");

            testTaskInstanceId = first.getId().longValue();
            testTaskInstanceIdInt = first.getId();
            testProjectCode = first.getProjectCode();
            log.info("找到任务实例用于测试: id={}, name={}, state={}", 
                    testTaskInstanceId, first.getName(), first.getState());
        } else {
            log.warn("分页查询未返回任务实例数据，部分测试可能被跳过");
        }
    }

    @Test
    @Order(2)
    public void test_02_queryTaskInstanceByCode() throws Exception {
        Assumptions.assumeTrue(testTaskInstanceId != null && testProjectCode != null, 
                "前置查询未找到任务实例，跳过");

        Response<TaskInstanceQueryResponseDTO> resp = taskInstanceApi.queryTaskInstanceByCode(
                testProjectCode, testTaskInstanceId).execute();

        Assertions.assertTrue(resp.isSuccessful(), "HTTP 状态应成功");
        TaskInstanceQueryResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");
        assertSuccess(body.getCode(), body.getSuccess(), body.getMsg());

        TaskInstanceDTO data = body.getData();
        Assertions.assertNotNull(data, "任务实例数据不应为空");
        Assertions.assertEquals(testTaskInstanceIdInt, data.getId(), "返回的 id 应匹配");
        Assertions.assertNotNull(data.getName(), "name 不应为空");
        Assertions.assertNotNull(data.getTaskType(), "taskType 不应为空");
        Assertions.assertNotNull(data.getState(), "state 不应为空");

        // 验证关键字段类型
        Assertions.assertNotNull(data.getProjectCode(), "projectCode 不应为空");
        Assertions.assertNotNull(data.getTaskCode(), "taskCode 不应为空");
        
        log.info("查询任务实例详情成功: id={}, name={}, type={}, state={}", 
                data.getId(), data.getName(), data.getTaskType(), data.getState());
    }

    @Test
    @Order(3)
    @Disabled("此测试会实际停止任务实例，仅在安全环境下手动启用")
    public void test_03_stopTask() throws Exception {
        Assumptions.assumeTrue(testTaskInstanceIdInt != null && testProjectCode != null, 
                "前置查询未找到任务实例，跳过");

        Response<TaskInstanceStopResponseDTO> resp = taskInstanceApi.stopTask(
                testProjectCode, testTaskInstanceIdInt).execute();

        Assertions.assertTrue(resp.isSuccessful(), "HTTP 状态应成功");
        TaskInstanceStopResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");
        
        // 注意：停止操作可能失败（如任务已完成），这里只验证接口通畅性
        log.info("停止任务实例响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());
    }

    @Test
    @Order(4)
    @Disabled("此测试会实际保存任务 SavePoint，仅在安全环境下手动启用")
    public void test_04_taskSavePoint() throws Exception {
        Assumptions.assumeTrue(testTaskInstanceIdInt != null && testProjectCode != null, 
                "前置查询未找到任务实例，跳过");

        Response<TaskInstanceSavePointResponseDTO> resp = taskInstanceApi.taskSavePoint(
                testProjectCode, testTaskInstanceIdInt).execute();

        Assertions.assertTrue(resp.isSuccessful(), "HTTP 状态应成功");
        TaskInstanceSavePointResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");
        
        log.info("任务 SavePoint 响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());
    }

    @Test
    @Order(5)
    @Disabled("此测试会强制任务成功，仅在安全环境下手动启用")
    public void test_05_forceTaskSuccess() throws Exception {
        Assumptions.assumeTrue(testTaskInstanceIdInt != null && testProjectCode != null, 
                "前置查询未找到任务实例，跳过");

        Response<TaskInstanceForceSuccessResponseDTO> resp = taskInstanceApi.forceTaskSuccess(
                testProjectCode, testTaskInstanceIdInt).execute();

        Assertions.assertTrue(resp.isSuccessful(), "HTTP 状态应成功");
        TaskInstanceForceSuccessResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");
        
        // 强制成功操作可能失败（如任务未运行），这里只验证接口通畅性
        log.info("强制任务成功响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());
    }

    @Test
    @Order(6)
    public void test_06_queryWithFilters() throws Exception {
        Assumptions.assumeTrue(testProjectCode != null, "未找到可用项目，跳过");

        // 测试带多种过滤条件的查询
        Response<TaskInstancePageResponseDTO> resp = taskInstanceApi.queryTaskListPaging(
                testProjectCode,
                null,     // workflowInstanceId
                null,     // workflowInstanceName
                "",       // searchVal
                null,     // taskName
                null,     // taskCode
                null,     // executorName
                "SUCCESS", // stateType - 查询成功状态的任务
                null,     // host
                null,     // startDate
                null,     // endDate
                "BATCH",  // taskExecuteType
                1,
                10
        ).execute();

        Assertions.assertTrue(resp.isSuccessful(), "HTTP 状态应成功");
        TaskInstancePageResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");
        assertSuccess(body.getCode(), body.getSuccess(), body.getMsg());

        log.info("带过滤条件查询完成，返回 {} 条记录", 
                body.getData() != null && body.getData().getTotalList() != null 
                        ? body.getData().getTotalList().size() : 0);
    }

    /**
     * DolphinScheduler 通用成功判断：
     * - 优先 code == 0
     * - 其次 success == true
     * - 再次 msg 包含 "success"（部分版本返回）
     */
    private static void assertSuccess(Integer code, Boolean success, String msg) {
        boolean ok = (code != null && code == 0)
                || Boolean.TRUE.equals(success)
                || (msg != null && msg.toLowerCase().contains("success"));
        Assertions.assertTrue(ok, () -> "接口返回失败: code=" + code + ", success=" + success + ", msg=" + msg);
    }
}
