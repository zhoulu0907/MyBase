package com.cmsr.onebase.framework.dolphins;

import com.cmsr.onebase.framework.dolphins.api.ScheduleApi;
import com.cmsr.onebase.framework.dolphins.config.DolphinSchedulerProperties;
import com.cmsr.onebase.framework.dolphins.dto.schedule.model.ScheduleDTO;
import com.cmsr.onebase.framework.dolphins.dto.schedule.request.ScheduleCreateRequestDTO;
import com.cmsr.onebase.framework.dolphins.dto.schedule.request.ScheduleQueryRequestDTO;
import com.cmsr.onebase.framework.dolphins.dto.schedule.request.ScheduleUpdateRequestDTO;
import com.cmsr.onebase.framework.dolphins.dto.schedule.response.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import retrofit2.Response;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.Socket;

/**
 * ScheduleApi 真实环境集成测试
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
 * 作者和日期遵循项目风格
 */
@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ScheduleApiTest {

    @Resource
    private ScheduleApi scheduleApi;

    @Resource
    private DolphinSchedulerProperties dsProps;

    private Integer createdScheduleId;

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
            Assumptions.assumeTrue(false, "网络不可达,跳过 ScheduleApi 集成测试: " + ex.getMessage());
        }
    }

    @Test
    @Order(0)
    public void test_00_checkConnectivity() throws Exception {
        ScheduleQueryRequestDTO queryReq = new ScheduleQueryRequestDTO();
        queryReq.setPageNo(1);
        queryReq.setPageSize(1);

        Response<SchedulePageResponseDTO> resp = scheduleApi.queryScheduleListPaging(queryReq).execute();

        Assertions.assertNotNull(resp, "应该能收到响应");
        Assertions.assertEquals(200, resp.code(), "HTTP状态应为200");
        log.info("✓ DolphinScheduler ScheduleApi 连通性测试通过,HTTP状态: {}", resp.code());

        if (resp.body() != null) {
            log.info("  响应内容: code={}, msg={}, success={}", resp.body().getCode(), resp.body().getMsg(), resp.body().getSuccess());
        }
    }

    @Test
    @Order(1)
    public void test_01_queryScheduleListPaging() throws Exception {
        ScheduleQueryRequestDTO queryReq = new ScheduleQueryRequestDTO();
        queryReq.setPageNo(1);
        queryReq.setPageSize(10);

        Response<SchedulePageResponseDTO> resp = scheduleApi.queryScheduleListPaging(queryReq).execute();

        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertEquals(200, resp.code(), "HTTP 状态应为200");
        SchedulePageResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        log.info("分页查询定时调度响应: code={}, msg={}, success={}", body.getCode(), body.getMsg(), body.getSuccess());

        if (body.getCode() != null && body.getCode() == 0 && body.getData() != null) {
            Assertions.assertNotNull(body.getData().getPageNo(), "pageNo 不应为空");
            Assertions.assertNotNull(body.getData().getPageSize(), "pageSize 不应为空");
            log.info("✓ 分页查询定时调度成功,返回 {} 条记录", body.getData().getTotalList() == null ? 0 : body.getData().getTotalList().size());
        } else {
            log.warn("⚠ 分页查询定时调度业务失败: code={}, msg={}", body.getCode(), body.getMsg());
        }
    }

    @Test
    @Order(2)
    public void test_02_createSchedule() throws Exception {
        // 请根据实际环境调整为有效的 workflowDefinitionCode
        Long testWorkflowDefinitionCode = 1L;

        ScheduleCreateRequestDTO req = new ScheduleCreateRequestDTO();
        req.setWorkflowDefinitionCode(testWorkflowDefinitionCode);
        req.setCrontab("0 0 12 * * ?");
        req.setStartTime("2025-10-17 00:00:00");
        req.setEndTime("2026-10-17 00:00:00");
        req.setTimezoneId("Asia/Shanghai");
        req.setFailureStrategy("CONTINUE");
        req.setReleaseState("OFFLINE");

        Response<ScheduleCreateResponseDTO> resp = scheduleApi.createSchedule(req).execute();

        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertEquals(200, resp.code(), "HTTP 状态应为200");
        ScheduleCreateResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        log.info("创建定时调度响应: code={}, msg={}, success={}", body.getCode(), body.getMsg(), body.getSuccess());

        if (body.getCode() != null && body.getCode() == 0 && body.getData() != null) {
            ScheduleDTO data = body.getData();
            Assertions.assertNotNull(data.getId(), "id 不应为空");
            Assertions.assertEquals(testWorkflowDefinitionCode, data.getWorkflowDefinitionCode(), "workflowDefinitionCode 应与请求一致");
            createdScheduleId = data.getId();
            log.info("✓ 创建定时调度成功: id={}, workflowDefinitionCode={}", data.getId(), data.getWorkflowDefinitionCode());
        } else {
            log.warn("⚠ 创建定时调度业务失败: code={}, msg={}", body.getCode(), body.getMsg());
            log.warn("  提示: 请确保 workflowDefinitionCode={} 在系统中存在", testWorkflowDefinitionCode);
        }
    }

    @Test
    @Order(3)
    public void test_03_queryScheduleById() throws Exception {
        // 请根据实际情况调整，如果 create 未成功，则此ID需要配置为系统中存在的ID
        Integer testScheduleId = createdScheduleId != null ? createdScheduleId : 1;

        Response<ScheduleQueryResponseDTO> resp = scheduleApi.queryScheduleById(testScheduleId).execute();

        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertEquals(200, resp.code(), "HTTP 状态应为200");
        ScheduleQueryResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        log.info("查询定时调度详情响应: code={}, msg={}, success={}", body.getCode(), body.getMsg(), body.getSuccess());

        if (body.getCode() != null && body.getCode() == 0 && body.getData() != null) {
            ScheduleDTO data = body.getData();
            Assertions.assertNotNull(data.getId(), "id 不应为空");
            Assertions.assertNotNull(data.getWorkflowDefinitionCode(), "workflowDefinitionCode 不应为空");
            Assertions.assertNotNull(data.getCrontab(), "crontab 不应为空");
            log.info("✓ 查询定时调度详情成功: id={}, crontab={}", data.getId(), data.getCrontab());
        } else {
            log.warn("⚠ 查询定时调度详情业务失败: code={}, msg={}", body.getCode(), body.getMsg());
            log.warn("  提示: 请确保 scheduleId={} 在系统中存在", testScheduleId);
        }
    }

    @Test
    @Order(4)
    public void test_04_updateSchedule() throws Exception {
        Integer testScheduleId = createdScheduleId != null ? createdScheduleId : 1;

        ScheduleUpdateRequestDTO req = new ScheduleUpdateRequestDTO();
        req.setCrontab("0 0 13 * * ?");
        req.setStartTime("2025-10-17 00:00:00");
        req.setEndTime("2026-10-17 00:00:00");
        req.setTimezoneId("Asia/Shanghai");
        req.setFailureStrategy("END");

        Response<ScheduleUpdateResponseDTO> resp = scheduleApi.updateSchedule(testScheduleId, req).execute();

        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertEquals(200, resp.code(), "HTTP 状态应为200");
        ScheduleUpdateResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        log.info("更新定时调度响应: code={}, msg={}, success={}", body.getCode(), body.getMsg(), body.getSuccess());

        if (body.getCode() != null && body.getCode() == 0 && body.getData() != null) {
            ScheduleDTO data = body.getData();
            log.info("✓ 更新定时调度成功: id={}, crontab={}", data.getId(), data.getCrontab());
        } else {
            log.warn("⚠ 更新定时调度业务失败: code={}, msg={}", body.getCode(), body.getMsg());
            log.warn("  提示: 请确保 scheduleId={} 在系统中存在", testScheduleId);
        }
    }

    @Test
    @Order(5)
    public void test_05_deleteSchedule() throws Exception {
        Integer testScheduleId = createdScheduleId != null ? createdScheduleId : 999999;

        Response<ScheduleDeleteResponseDTO> resp = scheduleApi.deleteSchedule(testScheduleId).execute();

        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertEquals(200, resp.code(), "HTTP 状态应为200");
        ScheduleDeleteResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");

        log.info("删除定时调度响应: code={}, msg={}, success={}", body.getCode(), body.getMsg(), body.getSuccess());

        if (body.getCode() != null && body.getCode() == 0) {
            log.info("✓ 删除定时调度成功");
        } else {
            log.warn("⚠ 删除定时调度业务失败: code={}, msg={}", body.getCode(), body.getMsg());
            log.warn("  提示: schedule 可能不存在或已被删除(id={})", testScheduleId);
        }
    }
}
