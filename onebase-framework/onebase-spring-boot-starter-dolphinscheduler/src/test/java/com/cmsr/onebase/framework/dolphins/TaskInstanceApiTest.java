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
        Long testProjectCodeForPaging = 123456789L; // 请根据实际环境修改此值

        Response<TaskInstancePageResponseDTO> resp = taskInstanceApi.queryTaskListPaging(
                testProjectCodeForPaging,
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

        // 只验证HTTP响应成功
        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertTrue(resp.isSuccessful(), "HTTP 状态应成功");
        TaskInstancePageResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");
        
        log.info("分页查询任务实例响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());

        // 如果接口调用成功（有数据），则进行后续验证
        if (body.getCode() != null && body.getCode() == 0 && body.getData() != null) {
            // 验证分页结构
            Assertions.assertNotNull(body.getData().getPageNo(), "pageNo 不应为空");
            Assertions.assertNotNull(body.getData().getPageSize(), "pageSize 不应为空");

            // 如果有数据，记录任务实例信息
            if (body.getData().getTotalList() != null && !body.getData().getTotalList().isEmpty()) {
                TaskInstanceDTO first = body.getData().getTotalList().get(0);
                Assertions.assertNotNull(first.getId(), "任务实例 ID 不能为空");
                Assertions.assertNotNull(first.getName(), "任务实例 name 不能为空");
                Assertions.assertNotNull(first.getState(), "任务实例 state 不能为空");
                Assertions.assertNotNull(first.getProjectCode(), "任务实例 projectCode 不能为空");

                log.info("找到任务实例: id={}, name={}, state={}", 
                        first.getId(), first.getName(), first.getState());
            } else {
                log.warn("项目中暂无任务实例数据");
            }
        } else {
            // 接口返回业务错误（如项目不存在），记录日志但不失败
            log.warn("分页查询任务实例业务失败: code={}, msg={}", body.getCode(), body.getMsg());
            log.warn("提示：请确保 testProjectCode={} 是系统中存在的项目编码", testProjectCodeForPaging);
        }
    }

    @Test
    @Order(2)
    public void test_02_queryTaskInstanceByCode() throws Exception {
        // 使用固定的测试参数进行独立测试
        Long testProjectCodeForQuery = 123456789L; // 请根据实际环境修改
        Long testTaskInstanceIdForQuery = 1L; // 请根据实际环境修改

        Response<TaskInstanceQueryResponseDTO> resp = taskInstanceApi.queryTaskInstanceByCode(
                testProjectCodeForQuery, testTaskInstanceIdForQuery).execute();

        // 只验证HTTP响应成功
        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertTrue(resp.isSuccessful(), "HTTP 状态应成功");
        TaskInstanceQueryResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");
        
        log.info("查询任务实例详情响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());

        // 如果接口调用成功，则进行字段验证
        if (body.getCode() != null && body.getCode() == 0 && body.getData() != null) {
            TaskInstanceDTO data = body.getData();
            Assertions.assertNotNull(data.getId(), "id 不应为空");
            Assertions.assertNotNull(data.getName(), "name 不应为空");
            Assertions.assertNotNull(data.getTaskType(), "taskType 不应为空");
            Assertions.assertNotNull(data.getState(), "state 不应为空");

            // 验证关键字段类型
            Assertions.assertNotNull(data.getProjectCode(), "projectCode 不应为空");
            Assertions.assertNotNull(data.getTaskCode(), "taskCode 不应为空");
            
            log.info("查询任务实例详情成功: id={}, name={}, type={}, state={}", 
                    data.getId(), data.getName(), data.getTaskType(), data.getState());
        } else {
            log.warn("查询任务实例详情业务失败: code={}, msg={}", body.getCode(), body.getMsg());
            log.warn("提示：请确保 projectCode={}, taskInstanceId={} 是系统中存在的任务实例", 
                    testProjectCodeForQuery, testTaskInstanceIdForQuery);
        }
    }

    @Test
    @Order(3)
    public void test_03_stopTask() throws Exception {
        // 使用固定的测试参数进行独立测试
        Long testProjectCodeForStop = 123456789L; // 请根据实际环境修改
        Integer testTaskInstanceIdForStop = 1; // 请根据实际环境修改

        Response<TaskInstanceStopResponseDTO> resp = taskInstanceApi.stopTask(
                testProjectCodeForStop, testTaskInstanceIdForStop).execute();

        // 只验证HTTP响应成功
        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertTrue(resp.isSuccessful(), "HTTP 状态应成功");
        TaskInstanceStopResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        // 停止任务可能失败（例如任务已结束、不存在等），只验证响应结构
        log.info("停止任务响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());
        
        if (body.getCode() != null && body.getCode() != 0) {
            log.warn("停止任务业务失败: code={}, msg={}", body.getCode(), body.getMsg());
            log.warn("提示：请确保 projectCode={}, taskInstanceId={} 是系统中存在且正在运行的任务实例", 
                    testProjectCodeForStop, testTaskInstanceIdForStop);
        }
    }

    @Test
    @Order(4)
    public void test_04_taskSavePoint() throws Exception {
        // 使用固定的测试参数进行独立测试
        Long testProjectCodeForSavePoint = 123456789L; // 请根据实际环境修改
        Integer testTaskInstanceIdForSavePoint = 1; // 请根据实际环境修改

        Response<TaskInstanceSavePointResponseDTO> resp = taskInstanceApi.taskSavePoint(
                testProjectCodeForSavePoint, testTaskInstanceIdForSavePoint).execute();

        // 只验证HTTP响应成功
        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertTrue(resp.isSuccessful(), "HTTP 状态应成功");
        TaskInstanceSavePointResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        // 保存点操作可能失败（例如任务类型不支持、任务不存在等），只验证响应结构
        log.info("任务保存点响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());
        
        if (body.getCode() != null && body.getCode() != 0) {
            log.warn("任务保存点业务失败: code={}, msg={}", body.getCode(), body.getMsg());
            log.warn("提示：保存点功能通常仅支持流式任务（如Flink），请确保任务类型正确");
        }
    }

    @Test
    @Order(5)
    public void test_05_forceTaskSuccess() throws Exception {
        // 使用固定的测试参数进行独立测试
        Long testProjectCodeForForce = 123456789L; // 请根据实际环境修改
        Integer testTaskInstanceIdForForce = 1; // 请根据实际环境修改

        Response<TaskInstanceForceSuccessResponseDTO> resp = taskInstanceApi.forceTaskSuccess(
                testProjectCodeForForce, testTaskInstanceIdForForce).execute();

        // 只验证HTTP响应成功
        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertTrue(resp.isSuccessful(), "HTTP 状态应成功");
        TaskInstanceForceSuccessResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        // 强制成功操作可能失败（例如任务状态不允许、任务不存在等），只验证响应结构
        log.info("强制任务成功响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());
        
        if (body.getCode() != null && body.getCode() != 0) {
            log.warn("强制任务成功业务失败: code={}, msg={}", body.getCode(), body.getMsg());
            log.warn("提示：请确保 projectCode={}, taskInstanceId={} 是系统中存在且状态允许操作的任务实例", 
                    testProjectCodeForForce, testTaskInstanceIdForForce);
        }
    }

    @Test
    @Order(6)
    public void test_06_queryWithFilters() throws Exception {
        // 使用固定的测试参数进行独立测试
        Long testProjectCodeForFilter = 123456789L; // 请根据实际环境修改

        // 测试带多种过滤条件的查询
        Response<TaskInstancePageResponseDTO> resp = taskInstanceApi.queryTaskListPaging(
                testProjectCodeForFilter,
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

        // 只验证HTTP响应成功
        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertTrue(resp.isSuccessful(), "HTTP 状态应成功");
        TaskInstancePageResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        log.info("带过滤条件查询响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());
        
        int recordCount = (body.getData() != null && body.getData().getTotalList() != null) 
                ? body.getData().getTotalList().size() : 0;
        log.info("带过滤条件查询完成,返回 {} 条记录", recordCount);
    }
}
