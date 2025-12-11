package com.cmsr.onebase.module.flow.graph;

import com.cmsr.onebase.module.flow.api.FlowProcessExecApiImpl;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.flow.FlowRemoteCallExecutor;
import com.cmsr.onebase.module.flow.core.flow.FlowRemoteCallRequest;
import com.cmsr.onebase.module.flow.runtime.service.FlowProcessExecService;
import com.cmsr.onebase.server.runtime.OneBaseServerRuntimeApplication;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@Setter
@SpringBootTest(classes = OneBaseServerRuntimeApplication.class)
public class FlowProcessDateTest {


    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Autowired
    private FlowProcessExecApiImpl flowProcessExecApi;

    @Autowired//日期触发用
    private FlowRemoteCallExecutor flowRemoteCallExecutor;

    @Autowired
    private FlowProcessExecService flowProcessExecService;


    //表单实体测试  前置 创建前 过滤条件 TEXT 字段类型 等于 公式 CONCATENATE("new","123")
    @Test
    public void testEntity1() throws IOException {
//        115219301059362816 flowid
        FlowRemoteCallRequest jobMessage = new FlowRemoteCallRequest();
//        jobMessage.setTime(LocalDateTime.now().toString());
//        jobMessage.setTime("2025-11-05 15:35");
        jobMessage.setJobType("fld");
        jobMessage.setProcessId(115219301059362816L);
        flowRemoteCallExecutor.executeFlow(jobMessage);
        System.out.println("===========================日期触发===============");

    }


    //表单实体测试  前置 创建前 过滤条件 TEXT 字段类型 等于 公式 CONCATENATE("new","123")
    @Test
    public void testEntity2() throws IOException {
//        115219301059362816 flowid
        FlowRemoteCallRequest jobMessage = new FlowRemoteCallRequest();
        jobMessage.setJobType("fld");
        jobMessage.setProcessId(115219301059362816L);
        flowRemoteCallExecutor.executeFlow(jobMessage);
        System.out.println("===========================日期触发===============");

    }


}
