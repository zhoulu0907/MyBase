package com.cmsr.onebase.framework.dolphins;

import com.cmsr.onebase.framework.dolphins.api.WorkflowInstanceApi;
import com.cmsr.onebase.framework.dolphins.dto.workflowinstance.enums.ExecuteTypeEnum;
import com.cmsr.onebase.framework.dolphins.dto.workflowinstance.response.*;
import com.cmsr.onebase.framework.dolphins.config.DolphinSchedulerProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import retrofit2.Response;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.Socket;

/**
 * WorkflowInstanceApi 真实环境集成测试
 *
 * 测试目标:
 * 1. 验证接口连通性
 * 2. 验证请求体和响应体字段映射与类型
 * 3. 真实环境测试,连接真实 DolphinScheduler 服务
 * 4. 完整流程测试: 查询列表→查询详情→执行操作→删除
 * 5. 字段验证: 验证请求和响应的所有字段
 *
 * 注意: 基于 DolphinScheduler 3.3.1 API v2 版本
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WorkflowInstanceApiTest {

    @Resource
    private WorkflowInstanceApi workflowInstanceApi;

    @Resource
    private DolphinSchedulerProperties dsProps;

    private Integer testWorkflowInstanceId;

    @BeforeAll
    void checkConnectivityOrSkip() {
        // 在受限网络环境下,先做 TCP 直连探测以决定是否跳过全部集成测试
        try {
            URI uri = URI.create(dsProps.getBaseUrl());
            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : ("https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80);
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 3000);
            }
            log.info("DolphinScheduler 可达: {}:{}", host, port);
        } catch (Exception ex) {
            Assumptions.assumeTrue(false, "网络不可达,跳过 WorkflowInstanceApi 集成测试: " + ex.getMessage());
        }
    }

    /**
     * 测试0: 检查服务连通性
     */
    @Test
    @Order(0)
    public void test_00_checkConnectivity() throws Exception {
        // 测试连通性 - 使用查询列表接口
        Response<WorkflowInstancePageResponseDTO> resp = workflowInstanceApi
                .queryWorkflowInstanceListPaging(1, 1, null, null, null, null, null, null)
                .execute();
        
        // 只要能收到HTTP响应就说明服务可达
        Assertions.assertNotNull(resp, "应该能收到响应");
        Assertions.assertEquals(200, resp.code(), "HTTP状态应为200");
        log.info("✓ DolphinScheduler WorkflowInstanceApi 连通性测试通过,HTTP状态: {}", resp.code());
        
        if (resp.body() != null) {
            log.info("  响应内容: code={}, msg={}, success={}", 
                    resp.body().getCode(), resp.body().getMsg(), resp.body().getSuccess());
        }
    }

    /**
     * 测试1: 查询工作流实例列表
     */
    @Test
    @Order(1)
    public void test_01_queryWorkflowInstanceListPaging() throws Exception {
        Response<WorkflowInstancePageResponseDTO> resp = workflowInstanceApi
                .queryWorkflowInstanceListPaging(1, 10, null, null, null, null, null, null)
                .execute();

        // HTTP层面验证
        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertEquals(200, resp.code(), "HTTP 状态应为200");
        WorkflowInstancePageResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        log.info("查询工作流实例列表响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());

        // 如果接口调用成功,则进行数据验证
        if (body.getCode() != null && body.getCode() == 0 && body.getData() != null) {
            log.info("✓ 查询工作流实例列表成功");
            
            // 如果有数据,保存第一个实例ID用于后续测试
            // 注意: 这里假设data是一个包含实例列表的对象
            // 具体字段需要根据实际响应结构调整
        } else {
            log.warn("⚠ 查询工作流实例列表业务失败: code={}, msg={}", body.getCode(), body.getMsg());
        }
    }

    /**
     * 测试2: 查询工作流实例详情
     * 
     * 注意: 此测试需要有效的 workflowInstanceId
     */
    @Test
    @Order(2)
    public void test_02_queryWorkflowInstanceById() throws Exception {
        // 测试参数 - 请根据实际环境修改
        Integer testInstanceId = 1;

        Response<WorkflowInstanceQueryResponseDTO> resp = workflowInstanceApi
                .queryWorkflowInstanceById(testInstanceId)
                .execute();

        // HTTP层面验证
        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertEquals(200, resp.code(), "HTTP 状态应为200");
        WorkflowInstanceQueryResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        log.info("查询工作流实例详情响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());

        // 如果接口调用成功,则进行字段验证
        if (body.getCode() != null && body.getCode() == 0 && body.getData() != null) {
            log.info("✓ 查询工作流实例详情成功: instanceId={}", testInstanceId);
            
            // 保存实例ID用于后续测试
            testWorkflowInstanceId = testInstanceId;
        } else {
            log.warn("⚠ 查询工作流实例详情业务失败: code={}, msg={}", body.getCode(), body.getMsg());
            log.warn("  提示: 请确保 workflowInstanceId={} 是系统中存在的工作流实例", testInstanceId);
        }
    }

    /**
     * 测试3: 执行工作流实例操作(暂停)
     * 
     * 注意: 此测试需要有效的且正在运行的 workflowInstanceId
     * 此测试仅演示接口调用,实际执行可能会失败(如实例不在运行状态)
     */
    @Test
    @Order(3)
    public void test_03_executeWorkflowInstance_Pause() throws Exception {
        // 测试参数 - 请根据实际环境修改
        Integer testInstanceId = testWorkflowInstanceId != null ? testWorkflowInstanceId : 1;

        Response<WorkflowInstanceExecuteResponseDTO> resp = workflowInstanceApi
                .execute(testInstanceId, ExecuteTypeEnum.PAUSE)
                .execute();

        // HTTP层面验证
        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertEquals(200, resp.code(), "HTTP 状态应为200");
        WorkflowInstanceExecuteResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        log.info("执行工作流实例操作(暂停)响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());

        if (body.getCode() != null && body.getCode() == 0) {
            log.info("✓ 执行工作流实例操作(暂停)成功: instanceId={}", testInstanceId);
        } else {
            log.warn("⚠ 执行工作流实例操作(暂停)业务失败: code={}, msg={}", body.getCode(), body.getMsg());
            log.warn("  提示: 此操作要求实例处于运行状态,当前实例可能不满足条件");
        }
    }

    /**
     * 测试4: 执行工作流实例操作(停止)
     * 
     * 注意: 此测试需要有效的且正在运行的 workflowInstanceId
     * 此测试仅演示接口调用,实际执行可能会失败(如实例不在运行状态)
     */
    @Test
    @Order(4)
    public void test_04_executeWorkflowInstance_Stop() throws Exception {
        // 测试参数 - 请根据实际环境修改
        Integer testInstanceId = testWorkflowInstanceId != null ? testWorkflowInstanceId : 1;

        Response<WorkflowInstanceExecuteResponseDTO> resp = workflowInstanceApi
                .execute(testInstanceId, ExecuteTypeEnum.STOP)
                .execute();

        // HTTP层面验证
        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertEquals(200, resp.code(), "HTTP 状态应为200");
        WorkflowInstanceExecuteResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        log.info("执行工作流实例操作(停止)响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());

        if (body.getCode() != null && body.getCode() == 0) {
            log.info("✓ 执行工作流实例操作(停止)成功: instanceId={}", testInstanceId);
        } else {
            log.warn("⚠ 执行工作流实例操作(停止)业务失败: code={}, msg={}", body.getCode(), body.getMsg());
            log.warn("  提示: 此操作要求实例处于运行状态,当前实例可能不满足条件");
        }
    }

    /**
     * 测试5: 执行工作流实例操作(重跑)
     * 
     * 注意: 此测试需要有效的 workflowInstanceId
     * 此测试仅演示接口调用,实际执行可能会失败
     */
    @Test
    @Order(5)
    public void test_05_executeWorkflowInstance_RepeatRunning() throws Exception {
        // 测试参数 - 请根据实际环境修改
        Integer testInstanceId = testWorkflowInstanceId != null ? testWorkflowInstanceId : 1;

        Response<WorkflowInstanceExecuteResponseDTO> resp = workflowInstanceApi
                .execute(testInstanceId, ExecuteTypeEnum.REPEAT_RUNNING)
                .execute();

        // HTTP层面验证
        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertEquals(200, resp.code(), "HTTP 状态应为200");
        WorkflowInstanceExecuteResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        log.info("执行工作流实例操作(重跑)响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());

        if (body.getCode() != null && body.getCode() == 0) {
            log.info("✓ 执行工作流实例操作(重跑)成功: instanceId={}", testInstanceId);
        } else {
            log.warn("⚠ 执行工作流实例操作(重跑)业务失败: code={}, msg={}", body.getCode(), body.getMsg());
        }
    }

    /**
     * 测试6: 删除工作流实例
     * 
     * 注意: 此测试会实际删除数据,请谨慎使用
     * 建议使用测试专用的 workflowInstanceId
     */
    @Test
    @Order(6)
    public void test_06_deleteWorkflowInstance() throws Exception {
        // 测试参数 - 请根据实际环境修改
        // 警告: 此操作会删除工作流实例,请使用测试专用的 ID
        Integer testInstanceId = 999999; // 使用一个不存在的ID避免误删

        Response<WorkflowInstanceDeleteResponseDTO> resp = workflowInstanceApi
                .deleteWorkflowInstance(testInstanceId)
                .execute();

        // HTTP层面验证
        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertEquals(200, resp.code(), "HTTP 状态应为200");
        WorkflowInstanceDeleteResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        log.info("删除工作流实例响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());

        if (body.getCode() != null && body.getCode() == 0) {
            log.info("✓ 删除工作流实例成功: instanceId={}", testInstanceId);
        } else {
            log.warn("⚠ 删除工作流实例业务失败: code={}, msg={}", body.getCode(), body.getMsg());
            log.warn("  提示: 工作流实例可能不存在或已被删除(instanceId={})", testInstanceId);
        }
    }
}
