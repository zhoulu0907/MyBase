package com.cmsr.onebase.module.flow;

import com.cmsr.onebase.module.flow.api.dto.EntityTriggerReqDTO;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerRespDTO;
import com.cmsr.onebase.module.flow.api.dto.TriggerEventEnum;
import com.cmsr.onebase.module.flow.context.condition.SimpleField;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.flow.FlowRemoteCallExecutor;
import com.cmsr.onebase.module.flow.core.graph.FlowChainBuilder;
import com.cmsr.onebase.module.flow.core.graph.FlowGraphBuilder;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * @Author：huangjie
 * @Date：2025/9/1 12:11
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
        reqDTO.setTableName("xzqd_student_info");
        reqDTO.setFlowContext(Map.of(
                SystemFieldConstants.REQUIRE.CREATOR, "155019577667616800",
                SystemFieldConstants.REQUIRE.UPDATER, "155019577667616800",
                SystemFieldConstants.REQUIRE.OWNER_ID, "155019577667616800",
                SystemFieldConstants.REQUIRE.OWNER_DEPT, "101"
        ));
        SemanticFieldValueDTO name = SemanticFieldValueDTO.ofType(SemanticFieldTypeEnum.TEXT);
        name.setFieldName("student_name");
        name.setRawValue("小");

        SemanticFieldValueDTO birthday = SemanticFieldValueDTO.ofType(SemanticFieldTypeEnum.DATE);
        birthday.setFieldName("birthday");
        birthday.setRawValue("2025-12-01");

        SemanticFieldValueDTO age = SemanticFieldValueDTO.ofType(SemanticFieldTypeEnum.NUMBER);
        age.setFieldName("gender");
        age.setRawValue(8);


        reqDTO.setFieldData(List.of(name, birthday, age));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println(respDTO);
    }

    @Test
    public void testFormTrigger01() throws IOException {
        FormTriggerReqVO reqVO = new FormTriggerReqVO();
        reqVO.setProcessId(181941429188165632L);
        reqVO.setInputParams(Map.of(
                "student_name", "小",
                "birthday", "2025-12-01"
        ));
        FormTriggerRespVO formTriggerRespVO = flowProcessExecService.triggerForm(reqVO);
        System.out.println(formTriggerRespVO);
    }

    @Test
    public void testFormTrigger02() throws IOException {
        FormTriggerReqVO reqVO = new FormTriggerReqVO();
        reqVO.setProcessId(181941429188165632L);
        reqVO.setExecutionUuid("3a156a8a-73c7-407c-af19-7119bf89fa48");

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
}
