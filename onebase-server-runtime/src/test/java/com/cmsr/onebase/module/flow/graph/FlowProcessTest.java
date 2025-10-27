package com.cmsr.onebase.module.flow.graph;

import com.cmsr.onebase.module.flow.api.FlowProcessExecApiImpl;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerReqDTO;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerRespDTO;
import com.cmsr.onebase.module.flow.api.dto.TriggerEventEnum;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.graph.JsonGraphBuilder;
import com.cmsr.onebase.module.flow.core.job.FlowJobMessage;
import com.cmsr.onebase.module.flow.core.job.FlowJobMessageHandler;
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
    private FlowJobMessageHandler flowJobMessageHandler;

    public void testToFlowChain(Long id) throws IOException {
        FlowProcessDO flowProcessDO = flowProcessRepository.findById(id);
        String json = flowProcessDO.getProcessDefinition();
        JsonGraph jsonGraph = JsonGraphBuilder.build(json);
        System.out.println(jsonGraph.toFlowChain());
    }

    @Test
    public void testSimple() throws IOException {
        testToFlowChain(84076905441918976L);
    }


    @Test
    public void testSimple2() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        reqDTO.setEntityId(46999363287089152L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_CREATE);
        reqDTO.setFieldData(Map.of(
                "46999569445519360", "6年级3班",
                "50026937276661762", LocalDate.now(),
                "50028191407505411", 30
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
                104845168301834240L, "yy",
                "104951150916075520", "yy"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println(respDTO);
    }

    @Test
    public void testSimple3() throws IOException {
        FlowJobMessage jobMessage = new FlowJobMessage();
        jobMessage.setJobType("fld");
        jobMessage.setProcessId(89995954500108288L);
        flowJobMessageHandler.executeFlow(jobMessage);
    }
}
