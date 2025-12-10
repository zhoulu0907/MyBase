package com.cmsr.onebase.module.flow;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.flow.api.FlowProcessExecApiImpl;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerReqDTO;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerRespDTO;
import com.cmsr.onebase.module.flow.api.dto.TriggerEventEnum;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.flow.FlowRemoteCallExecutor;
import com.cmsr.onebase.module.flow.core.graph.FlowChainBuilder;
import com.cmsr.onebase.module.flow.core.graph.FlowGraphBuilder;
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
public class FlowProcessTest {

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
    public void testFormTriggerReqVO01() throws IOException {
        ApplicationManager.ignoreApplicationCondition();
        ApplicationManager.ignoreVersionTagCondition();

        FormTriggerReqVO reqVO = new FormTriggerReqVO();
        reqVO.setProcessId(171999834000031744L);
        reqVO.setInputParams(Map.of());
        FormTriggerRespVO respVO = flowProcessExecService.triggerForm(reqVO);
        System.out.println(respVO);
    }

    @Test
    public void testEntityTriggerReqDTO01() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_CREATE);
        reqDTO.setApplicationId(173020283873034240L);
        reqDTO.setTableName("xzqd_student");
        reqDTO.setFlowContext(Map.of(
                SystemFieldConstants.REQUIRE.OWNER_ID, "155019577667616800",
                SystemFieldConstants.REQUIRE.CREATOR, "155019577667616800",
                SystemFieldConstants.REQUIRE.UPDATER, "155019577667616800"
        ));

        SemanticFieldValueDTO name = SemanticFieldValueDTO.ofType(SemanticFieldTypeEnum.TEXT);
        name.setFieldName("name");
        name.setRawValue("小");
        SemanticFieldValueDTO age = SemanticFieldValueDTO.ofType(SemanticFieldTypeEnum.NUMBER);
        age.setFieldName("age");
        age.setRawValue(8);

        reqDTO.setFieldData(List.of(name, age));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println(respDTO);
    }

}
