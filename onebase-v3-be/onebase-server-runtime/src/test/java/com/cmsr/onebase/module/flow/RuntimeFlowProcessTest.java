package com.cmsr.onebase.module.flow;

import com.cmsr.onebase.module.flow.api.dto.EntityTriggerReqDTO;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerRespDTO;
import com.cmsr.onebase.module.flow.api.dto.TriggerEventEnum;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.security.runtime.RTSecurityContext;
import com.cmsr.onebase.module.flow.context.condition.SimpleField;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowConnectorMapper;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowProcessMapper;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowConnectorTableDef;
import com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowProcessTableDef;
import com.cmsr.onebase.module.flow.core.flow.FlowRemoteCallExecutor;
import com.cmsr.onebase.module.flow.core.flow.FlowRemoteCallRequest;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.tenant.TenantManager;
import com.cmsr.onebase.module.flow.core.config.FlowProperties;
import com.cmsr.onebase.module.flow.core.graph.FlowChainBuilder;
import com.cmsr.onebase.module.flow.core.graph.FlowGraphBuilder;
import com.cmsr.onebase.module.flow.core.graph.FlowProcessCache;
import com.cmsr.onebase.module.flow.core.graph.FlowProcessManager;
import com.cmsr.onebase.module.flow.core.impl.FlowProcessExecApiImpl;
import com.cmsr.onebase.module.flow.runtime.service.FlowProcessExecService;
import com.cmsr.onebase.module.flow.runtime.vo.FormTriggerReqVO;
import com.cmsr.onebase.module.flow.runtime.vo.FormTriggerRespVO;
import com.cmsr.onebase.module.metadata.core.semantic.constants.SystemFieldConstants;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.server.runtime.OneBaseServerRuntimeApplication;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.*;

/**
 * @Author：huangjie
 *                  @Date：2025/9/1 12:11
 */
@Setter
@SpringBootTest(classes = OneBaseServerRuntimeApplication.class)
public class RuntimeFlowProcessTest {

    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Autowired
    private FlowProcessExecApiImpl flowProcessExecApi;

    @Autowired
    private FlowRemoteCallExecutor flowRemoteCallExecutor;

    @Autowired
    private FlowProcessExecService flowProcessExecService;

    @Autowired
    private FlowGraphBuilder flowGraphBuilder;

    @Autowired
    private FlowConnectorMapper flowConnectorMapper;

    @Autowired
    private FlowProcessManager flowProcessManager;

    @Autowired
    private FlowProperties flowProperties;

    @Autowired
    private FlowProcessMapper flowProcessMapper;

    public void testToFlowChain(Long id) throws IOException {
        FlowProcessDO flowProcessDO = flowProcessRepository.getById(id);
        String json = flowProcessDO.getProcessDefinition();
        JsonGraph jsonGraph = flowGraphBuilder.build(1L, json);
        String flowChain = FlowChainBuilder.toFlowChain(jsonGraph);
        System.out.println(flowChain);
    }

    @Test
    public void testEntityTrigger01() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_CREATE);
        reqDTO.setApplicationId(173020283873034240L);
        reqDTO.setTableName("xzqd_student_info2");
        reqDTO.setFlowContext(Map.of(
                SystemFieldConstants.REQUIRE.CREATOR, "155019577667616800",
                SystemFieldConstants.REQUIRE.UPDATER, "155019577667616800",
                SystemFieldConstants.REQUIRE.OWNER_ID, "155019577667616800",
                SystemFieldConstants.REQUIRE.OWNER_DEPT, "101"));
        SemanticFieldValueDTO name = SemanticFieldValueDTO.ofType(SemanticFieldTypeEnum.TEXT);
        name.setFieldName("student_name");
        name.setRawValue("小明");

        SemanticFieldValueDTO birthday = SemanticFieldValueDTO.ofType(SemanticFieldTypeEnum.DATE);
        birthday.setFieldName("birthday");
        birthday.setRawValue("2027-12-01");

        SemanticFieldValueDTO age = SemanticFieldValueDTO.ofType(SemanticFieldTypeEnum.TEXT);
        age.setFieldName("gender");
        age.setRawValue("男");

        reqDTO.setFieldData(List.of(name, birthday, age));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println(respDTO);
    }

    @Test
    public void testFormTrigger01() throws IOException {
        FormTriggerReqVO reqVO = new FormTriggerReqVO();
        reqVO.setProcessId(195502410512138240L);
        Map<String, Object> inputParams = new HashMap<>();
        inputParams.put("class_name", "3年级2班");
        List<Map<String, Object>> courses = new ArrayList<>();
        inputParams.put("xzqd_course_schedule", courses);
        {
            Map<String, Object> course = new HashMap<>();
            course.put("subject", "语文");
            course.put("classroom", "4教");
            courses.add(course);
        }
        {
            Map<String, Object> course = new HashMap<>();
            course.put("subject", "数学");
            course.put("classroom", "5教");
            courses.add(course);
        }

        reqVO.setInputParams(inputParams);
        FormTriggerRespVO formTriggerRespVO = flowProcessExecService.triggerForm(reqVO);
        System.out.println(formTriggerRespVO);
    }

    @Test
    public void testFormTrigger02() throws IOException {
        FormTriggerReqVO reqVO = new FormTriggerReqVO();
        reqVO.setProcessId(195502410512138240L);
        reqVO.setExecutionUuid("019b4f98-45e7-74ab-9b47-a07c8dd23b20");

        SimpleField field1 = new SimpleField();
        field1.setId("7VGW8DgADiKOFqlxU5JGG");
        field1.setFieldName("abc");
        field1.setFieldType("TEXT");
        field1.setValue("v1");

        SimpleField field2 = new SimpleField();
        field2.setId("pMtN_4Xz63M1jw31IUGqu");
        field2.setFieldName("abc");
        field2.setFieldType("TEXT");
        field2.setValue("v2");

        reqVO.setInputFields(List.of(field1, field2));
        FormTriggerRespVO formTriggerRespVO = flowProcessExecService.triggerForm(reqVO);
        System.out.println(formTriggerRespVO);
    }

    @Test
    public void testFormTrigger03() throws IOException {
        FormTriggerReqVO reqVO = new FormTriggerReqVO();
        reqVO.setProcessId(181943095635476480L);
        Map<String, Object> inputParams = new HashMap<>();
        inputParams.put("entity_date", null);
        reqVO.setInputParams(inputParams);
        FormTriggerRespVO formTriggerRespVO = flowProcessExecService.triggerForm(reqVO);
        System.out.println(formTriggerRespVO);
    }

    @Test
    public void testFlowRemoteCallExecutor() {
        FlowRemoteCallRequest callRequest = new FlowRemoteCallRequest();
        callRequest.setApplicationId(173020283873034240L);
        callRequest.setProcessId(193925435951546497L);
        callRequest.setProcessName("流程测试");
        callRequest.setJobType(FlowRemoteCallRequest.JOB_TYPE_FIELD);
        flowRemoteCallExecutor.executeFlow(callRequest);
    }

    /**
     * 测试包含 HTTP 连接器的流程
     *
     * 配置格式:
     * - connector.config: properties[envName].envConfig.basicInfo.baseUrl
     * - action_config: properties[actionName].debug.url/method
     *
     * 执行链路: triggerForm → FlowProcessCache → FlowProcessExecutor → LiteFlow → HttpNodeComponent
     */
    @Test
    public void testFormTrigger04() throws IOException {
        System.out.println("========== testFormTrigger04 开始 ==========");

        // Mock 登录用户，以通过 creator/updater 非空校验
        RTSecurityContext.mockLoginUser(1L, -1L);

        // 动态获取 versionTag，确保与 onApplicationChange 查询一致
        Long versionTag = flowProperties.getVersionTag();
        System.out.println("当前 versionTag = " + versionTag);

        long applicationId = 153935442021842944L;
        String connectorUuid = "019c0000-0000-0000-0001-httptest0001";
        String actionName = "GET请求测试";
        String envName = "DEV环境配置";
        long connectorId = System.nanoTime();
        long processId = System.nanoTime() + 1;

        // ===== 步骤1: 物理删除旧的脏数据（防止上次测试遗留） =====
        TenantManager.withoutTenantCondition(() ->
            LogicDeleteManager.execWithoutLogicDelete(() -> {
                flowConnectorMapper.deleteByQuery(
                        QueryWrapper.create().where(FlowConnectorTableDef.FLOW_CONNECTOR.CONNECTOR_UUID.eq(connectorUuid))
                );
                flowProcessMapper.deleteByQuery(
                        QueryWrapper.create().where(FlowProcessTableDef.FLOW_PROCESS.PROCESS_NAME.eq("HTTP连接器测试流程"))
                );
                return null;
            })
        );
        System.out.println("步骤1: 脏数据清理完成");

        try {
            // ===== 步骤2: 创建 HTTP 连接器 =====
            FlowConnectorDO connectorDO = new FlowConnectorDO();
            connectorDO.setId(connectorId);
            connectorDO.setConnectorUuid(connectorUuid);
            connectorDO.setConnectorName("HTTP测试连接器");
            connectorDO.setTypeCode("HTTP");
            connectorDO.setApplicationId(applicationId);
            connectorDO.setTenantId(-1L);
            connectorDO.setActiveStatus(1);
            connectorDO.setCreator(1L);
            connectorDO.setUpdater(1L);
            connectorDO.setDeleted(0L);

            // connector.config: 嵌套格式 properties[envName].envConfig.basicInfo
            String configJson = "{\"properties\":{\"" + envName + "\":{\"envConfig\":{" +
                    "\"basicInfo\":{\"baseUrl\":\"https://httpbin.org\",\"timeout\":10000}," +
                    "\"authInfo\":{\"authType\":\"none\"}" +
                    "}}}}";
            connectorDO.setConfig(configJson);

            // action_config: properties 格式 properties[actionName].debug.url/method
            String actionConfigJson = "{\"properties\":{\"" + actionName + "\":{" +
                    "\"debug\":{\"method\":\"GET\",\"url\":\"/get?test=formTrigger04\"}," +
                    "\"request\":{\"requestHeaders\":[],\"queryParams\":[],\"pathParams\":[],\"requestBody\":[]}" +
                    "}}}";
            connectorDO.setActionConfig(actionConfigJson);

            flowConnectorMapper.insert(connectorDO);
            System.out.println("步骤2: 连接器已插入, ID=" + connectorId);

            // ===== 步骤3: 创建流程定义 =====
            FlowProcessDO processDO = new FlowProcessDO();
            processDO.setId(processId);
            processDO.setProcessName("HTTP连接器测试流程");
            processDO.setProcessUuid(java.util.UUID.randomUUID().toString());
            processDO.setApplicationId(applicationId);
            processDO.setTenantId(-1L);
            processDO.setVersionTag(versionTag);
            processDO.setTriggerType("form");
            processDO.setEnableStatus(1);
            processDO.setPublishStatus(1);
            processDO.setCreator(1L);
            processDO.setUpdater(1L);
            processDO.setDeleted(0L);

            // 流程定义 JSON — 使用 actionName + envName（不再使用 httpUuid）
            String processJson = "{\"nodes\":[" +
                    "{\"id\":\"start_form_0\",\"type\":\"startForm\",\"data\":{\"id\":\"start_form_0\",\"triggerRange\":\"manual\",\"title\":\"表单触发节点\"},\"output\":{}}," +
                    "{\"id\":\"http_test_01\",\"type\":\"api_http\",\"tag\":\"httpTestNode\",\"data\":{\"id\":\"http_test_01\",\"nodeCode\":\"api_http\"," +
                    "\"actionName\":\"" + actionName + "\"," +
                    "\"envName\":\"" + envName + "\"," +
                    "\"connectorUuid\":\"" + connectorUuid + "\",\"title\":\"API Request\",\"retry\":0},\"output\":{}}," +
                    "{\"id\":\"end_0\",\"type\":\"end\",\"data\":{\"id\":\"end_0\",\"title\":\"结束\"},\"output\":{}}" +
                    "]}";
            processDO.setProcessDefinition(processJson);

            flowProcessMapper.insert(processDO);
            System.out.println("步骤3: 流程已插入, ID=" + processId);

            // ===== 步骤4: 直接构建并缓存本测试流程 =====
            // 临时禁用 completeFieldType（HTTP测试不需要字段类型补全）
            flowGraphBuilder.setFlowFieldTypeProvider((appId, graph) -> { });
            JsonGraph jsonGraph = flowGraphBuilder.build(applicationId, processJson, processId);
            FlowProcessCache.getInstance().update(processDO, jsonGraph);
            System.out.println("步骤4: FlowProcessCache已直接更新");

            // ===== 步骤5: 触发流程 =====
            FormTriggerReqVO reqVO = new FormTriggerReqVO();
            reqVO.setProcessId(processId);
            Map<String, Object> inputParams = new HashMap<>();
            inputParams.put("testParam", "testValue");
            reqVO.setInputParams(inputParams);

            System.out.println("步骤5: 触发流程...");
            FormTriggerRespVO formTriggerRespVO = flowProcessExecService.triggerForm(reqVO);

            System.out.println("========== 测试结果 ==========");
            System.out.println("成功: " + formTriggerRespVO.isSuccess());
            System.out.println("消息: " + formTriggerRespVO.getMessage());
            System.out.println("输出参数: " + formTriggerRespVO.getOutputParams());
            System.out.println("执行UUID: " + formTriggerRespVO.getExecutionUuid());

        } finally {
            // ===== 步骤6: 物理删除测试数据 =====
            TenantManager.withoutTenantCondition(() ->
                LogicDeleteManager.execWithoutLogicDelete(() -> {
                    flowConnectorMapper.deleteById(connectorId);
                    flowProcessMapper.deleteById(processId);
                    return null;
                })
            );
            System.out.println("步骤6: 数据清理完成（物理删除）");
        }

        System.out.println("========== testFormTrigger04 结束 ==========");
    }

}
