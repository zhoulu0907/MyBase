package com.cmsr.onebase.module.flow.graph;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.module.flow.api.FlowProcessExecApiImpl;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerReqDTO;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerRespDTO;
import com.cmsr.onebase.module.flow.api.dto.TriggerEventEnum;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.flow.FlowRemoteCallExecutor;
import com.cmsr.onebase.module.flow.core.flow.RemoteCallRequest;
import com.cmsr.onebase.module.flow.core.graph.FlowChainBuilder;
import com.cmsr.onebase.module.flow.core.graph.FlowGraphBuilder;
import com.cmsr.onebase.module.flow.runtime.service.FlowProcessExecService;
import com.cmsr.onebase.module.flow.runtime.vo.FormTriggerReqVO;
import com.cmsr.onebase.module.flow.runtime.vo.FormTriggerRespVO;
import com.cmsr.onebase.server.runtime.OneBaseServerRuntimeApplication;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDate;
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
    public void testSimple() throws IOException {
        testSimple2();
        testSimple2();
        testSimple2();
        testSimple2();
    }


    @Test
    public void testSimple2() throws IOException {
        ApplicationManager.ignoreApplicationCondition();
        ApplicationManager.ignoreVersionTagCondition();
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        reqDTO.setEntityId(165637750471655424L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_CREATE);
        reqDTO.setFieldData(Map.of(
//                "46999569445519360", "6年级3班",
//                "50026937276661762", LocalDate.now().minusYears(10),
//                "50028191407505411", 30
        ));
        //reqDTO.setChangedFieldIds(List.of(46999569445519360L));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println(respDTO);
    }

    @Test
    public void testSimple22() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        reqDTO.setEntityId(101573932216057856L);
        reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_UPDATE);
        reqDTO.setFieldData(Map.of(
                "104845168301834240", "yy",
                "104951150916075520", "yy"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println(respDTO);
    }

    @Test
    public void testSimple23() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        reqDTO.setEntityId(88576673965670400L);
        reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_UPDATE);
        reqDTO.setFieldData(Map.of(
                "8857773477298176", "测试部门号",
                "88577773477298181", "测试部门",
                "88577773477298186", "Manager"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println(respDTO);
    }

    @Test
    public void testSimple3() throws IOException {
        RemoteCallRequest jobMessage = new RemoteCallRequest();
        jobMessage.setJobType("fld");
        jobMessage.setProcessId(89995954500108288L);
        flowRemoteCallExecutor.executeFlow(jobMessage);
    }

    @Test
    public void testSimple4() throws IOException {
        TenantContextHolder.setIgnore(true);
        FormTriggerReqVO reqVO = new FormTriggerReqVO();
        reqVO.setProcessId(114994365031546880L);  // 114994365031546880L 114959369637036032L
        Map<Long, Object> inputParams = Map.of(
                46999569445519360L, "班级名称"
        );
        reqVO.setInputParams(inputParams);
        FormTriggerRespVO respVO = flowProcessExecService.triggerForm(reqVO);
        System.out.println(respVO);
    }

    @Test
    public void testSimple5() throws IOException {
        TenantContextHolder.setIgnore(true);
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        reqDTO.setEntityId(46999363287089152L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_CREATE);
        reqDTO.setFieldData(Map.of(
                "46999569445519360", "9年级145班",
                "50026937276661762", LocalDate.now().minusYears(10),
                "50028191407505411", 30
        ));
        //reqDTO.setChangedFieldIds(List.of(46999569445519360L));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println(respDTO);
    }
}
