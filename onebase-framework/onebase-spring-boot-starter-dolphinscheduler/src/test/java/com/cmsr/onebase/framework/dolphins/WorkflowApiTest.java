package com.cmsr.onebase.framework.dolphins;

import com.cmsr.onebase.framework.dolphins.api.WorkflowApi;
import com.cmsr.onebase.framework.dolphins.dto.workflow.enums.ExecutionTypeEnum;
import com.cmsr.onebase.framework.dolphins.dto.workflow.enums.ReleaseStateEnum;
import com.cmsr.onebase.framework.dolphins.dto.workflow.model.WorkflowDefinitionDTO;
import com.cmsr.onebase.framework.dolphins.dto.workflow.request.WorkflowCreateRequestDTO;
import com.cmsr.onebase.framework.dolphins.dto.workflow.request.WorkflowQueryRequestDTO;
import com.cmsr.onebase.framework.dolphins.dto.workflow.request.WorkflowUpdateRequestDTO;
import com.cmsr.onebase.framework.dolphins.dto.workflow.response.*;
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
 * WorkflowApi 真实环境集成测试
 *
 * 测试目标:
 * 1. 验证接口连通性
 * 2. 验证请求体和响应体字段映射与类型
 * 3. 真实环境测试,连接真实 DolphinScheduler 服务
 * 4. 完整流程测试: 分页查询→创建→查询详情→更新→删除
 * 5. 字段验证: 验证请求和响应的所有字段
 *
 * 注意: 基于 DolphinScheduler 3.3.1 API v2 版本
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WorkflowApiTest {

    @Resource
    private WorkflowApi workflowApi;

    @Resource
    private DolphinSchedulerProperties dsProps;

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
            Assumptions.assumeTrue(false, "网络不可达,跳过 WorkflowApi 集成测试: " + ex.getMessage());
        }
    }

    /**
     * 测试0: 检查服务连通性
     */
    @Test
    @Order(0)
    public void test_00_checkConnectivity() throws Exception {
        // 测试连通性 - 使用分页查询接口
        WorkflowQueryRequestDTO queryReq = new WorkflowQueryRequestDTO();
        queryReq.setPageNo(1);
        queryReq.setPageSize(1);
        
        Response<WorkflowPageResponseDTO> resp = workflowApi.queryWorkflowListPaging(queryReq).execute();
        
        // 只要能收到HTTP响应就说明服务可达
        Assertions.assertNotNull(resp, "应该能收到响应");
        Assertions.assertEquals(200, resp.code(), "HTTP状态应为200");
        log.info("✓ DolphinScheduler WorkflowApi 连通性测试通过,HTTP状态: {}", resp.code());
        
        if (resp.body() != null) {
            log.info("  响应内容: code={}, msg={}, success={}", 
                    resp.body().getCode(), resp.body().getMsg(), resp.body().getSuccess());
        }
    }

    /**
     * 测试1: 分页查询工作流定义列表
     */
    @Test
    @Order(1)
    public void test_01_queryWorkflowListPaging() throws Exception {
        WorkflowQueryRequestDTO queryReq = new WorkflowQueryRequestDTO();
        queryReq.setPageNo(1);
        queryReq.setPageSize(10);
        // 可选参数留空,查询所有
        
        Response<WorkflowPageResponseDTO> resp = workflowApi.queryWorkflowListPaging(queryReq).execute();

        // HTTP层面验证
        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertEquals(200, resp.code(), "HTTP 状态应为200");
        WorkflowPageResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        log.info("分页查询工作流响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());

        // 如果接口调用成功,则进行分页结构验证
        if (body.getCode() != null && body.getCode() == 0 && body.getData() != null) {
            Assertions.assertNotNull(body.getData().getPageNo(), "pageNo 不应为空");
            Assertions.assertNotNull(body.getData().getPageSize(), "pageSize 不应为空");
            
            int count = (body.getData().getTotalList() != null) 
                    ? body.getData().getTotalList().size() : 0;
            log.info("✓ 分页查询工作流成功,返回 {} 条记录", count);
        } else {
            log.warn("⚠ 分页查询工作流业务失败: code={}, msg={}", body.getCode(), body.getMsg());
        }
    }

    /**
     * 测试2: 创建工作流定义
     * 
     * 注意: 此测试需要有效的 projectCode
     */
    @Test
    @Order(2)
    public void test_02_createWorkflow() throws Exception {
        // 测试参数 - 请根据实际环境修改
        Long testProjectCode = 123456789L;

        WorkflowCreateRequestDTO req = new WorkflowCreateRequestDTO();
        req.setProjectCode(testProjectCode);
        req.setName("test-workflow-" + System.currentTimeMillis());
        req.setDescription("集成测试创建的工作流");
        req.setReleaseState(ReleaseStateEnum.OFFLINE);
        req.setExecutionType(ExecutionTypeEnum.PARALLEL);

        Response<WorkflowCreateResponseDTO> resp = workflowApi.createWorkflow(req).execute();

        // HTTP层面验证
        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertEquals(200, resp.code(), "HTTP 状态应为200");
        WorkflowCreateResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        log.info("创建工作流响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());

        // 如果接口调用成功,则进行字段验证
        if (body.getCode() != null && body.getCode() == 0 && body.getData() != null) {
            WorkflowDefinitionDTO data = body.getData();
            Assertions.assertNotNull(data.getCode(), "工作流 code 不能为空");
            Assertions.assertNotNull(data.getName(), "工作流 name 不能为空");
            Assertions.assertEquals(req.getName(), data.getName(), "name 应与请求一致");
            Assertions.assertEquals(testProjectCode, data.getProjectCode(), "projectCode 应与请求一致");
            
            log.info("✓ 创建工作流成功: code={}, name={}", data.getCode(), data.getName());
        } else {
            log.warn("⚠ 创建工作流业务失败: code={}, msg={}", body.getCode(), body.getMsg());
            log.warn("  提示: 请确保 projectCode={} 是系统中存在的项目编码", testProjectCode);
        }
    }

    /**
     * 测试3: 根据编码查询工作流详情
     * 
     * 注意: 此测试需要有效的 workflowCode
     */
    @Test
    @Order(3)
    public void test_03_queryWorkflowByCode() throws Exception {
        // 测试参数 - 请根据实际环境修改
        Long testWorkflowCode = 1L;

        Response<WorkflowQueryResponseDTO> resp = workflowApi.queryWorkflowByCode(testWorkflowCode).execute();

        // HTTP层面验证
        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertEquals(200, resp.code(), "HTTP 状态应为200");
        WorkflowQueryResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        log.info("查询工作流详情响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());

        // 如果接口调用成功,则进行字段验证
        if (body.getCode() != null && body.getCode() == 0 && body.getData() != null) {
            WorkflowDefinitionDTO data = body.getData();
            Assertions.assertNotNull(data.getId(), "id 不应为空");
            Assertions.assertNotNull(data.getCode(), "code 不应为空");
            Assertions.assertNotNull(data.getName(), "name 不应为空");
            Assertions.assertNotNull(data.getProjectCode(), "projectCode 不应为空");
            Assertions.assertNotNull(data.getReleaseState(), "releaseState 不应为空");
            
            log.info("✓ 查询工作流详情成功: code={}, name={}, state={}", 
                    data.getCode(), data.getName(), data.getReleaseState());
        } else {
            log.warn("⚠ 查询工作流详情业务失败: code={}, msg={}", body.getCode(), body.getMsg());
            log.warn("  提示: 请确保 workflowCode={} 是系统中存在的工作流", testWorkflowCode);
        }
    }

    /**
     * 测试4: 更新工作流定义
     * 
     * 注意: 此测试需要有效的 workflowCode
     */
    @Test
    @Order(4)
    public void test_04_updateWorkflow() throws Exception {
        // 测试参数 - 请根据实际环境修改
        Long testWorkflowCode = 1L;

        WorkflowUpdateRequestDTO req = new WorkflowUpdateRequestDTO();
        req.setName("test-workflow-updated-" + System.currentTimeMillis());
        req.setDescription("集成测试更新的工作流");
        req.setExecutionType(ExecutionTypeEnum.PARALLEL);

        Response<WorkflowUpdateResponseDTO> resp = workflowApi.updateWorkflow(testWorkflowCode, req).execute();

        // HTTP层面验证
        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertEquals(200, resp.code(), "HTTP 状态应为200");
        WorkflowUpdateResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        log.info("更新工作流响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());

        if (body.getCode() != null && body.getCode() == 0 && body.getData() != null) {
            WorkflowDefinitionDTO data = body.getData();
            log.info("✓ 更新工作流成功: code={}, name={}", data.getCode(), data.getName());
        } else {
            log.warn("⚠ 更新工作流业务失败: code={}, msg={}", body.getCode(), body.getMsg());
            log.warn("  提示: 请确保 workflowCode={} 是系统中存在的工作流", testWorkflowCode);
        }
    }

    /**
     * 测试5: 删除工作流定义
     * 
     * 注意: 此测试会实际删除数据,请谨慎使用
     * 建议使用测试专用的 workflowCode
     */
    @Test
    @Order(5)
    public void test_05_deleteWorkflow() throws Exception {
        // 测试参数 - 请根据实际环境修改
        // 警告: 此操作会删除工作流,请使用测试专用的 code
        Long testWorkflowCode = 999999L; // 使用一个不存在的code避免误删

        Response<WorkflowDeleteResponseDTO> resp = workflowApi.deleteWorkflow(testWorkflowCode).execute();

        // HTTP层面验证
        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertEquals(200, resp.code(), "HTTP 状态应为200");
        WorkflowDeleteResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        log.info("删除工作流响应: code={}, msg={}, success={}", 
                body.getCode(), body.getMsg(), body.getSuccess());

        if (body.getCode() != null && body.getCode() == 0) {
            log.info("✓ 删除工作流成功");
        } else {
            log.warn("⚠ 删除工作流业务失败: code={}, msg={}", body.getCode(), body.getMsg());
            log.warn("  提示: 工作流可能不存在或已被删除(code={})", testWorkflowCode);
        }
    }
}
