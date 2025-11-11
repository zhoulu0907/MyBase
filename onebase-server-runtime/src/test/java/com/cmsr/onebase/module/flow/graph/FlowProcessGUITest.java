package com.cmsr.onebase.module.flow.graph;

import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.module.flow.api.FlowProcessExecApiImpl;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.flow.ExecutorRequest;
import com.cmsr.onebase.module.flow.core.flow.FlowExecuteProvider;
import com.cmsr.onebase.module.flow.runtime.service.FlowProcessExecService;
import com.cmsr.onebase.module.flow.runtime.vo.FormTriggerReqVO;
import com.cmsr.onebase.module.flow.runtime.vo.FormTriggerRespVO;
import com.cmsr.onebase.server.runtime.OneBaseServerRuntimeApplication;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Setter
@SpringBootTest(classes = OneBaseServerRuntimeApplication.class)
public class FlowProcessGUITest {


        @Autowired
        private FlowProcessRepository flowProcessRepository;

        @Autowired
        private FlowProcessExecApiImpl flowProcessExecApi;

        @Autowired//日期触发用
        private FlowExecuteProvider flowExecuteProvider;

        @Autowired
        private FlowProcessExecService flowProcessExecService;

    @Test
    public void testGUIForm1() throws IOException {
        TenantContextHolder.setIgnore(true);
        FormTriggerReqVO reqVO = new FormTriggerReqVO();
        reqVO.setProcessId(124225813148991488L);  //
//        reqVO.setExecutionUuid("0ef919a7-f60f-4d24-8127-e0df6f29efb3");
        Map<Long, String> inputParams = Map.of(

        );
        reqVO.setInputParams(inputParams);
        FormTriggerRespVO respVO = flowProcessExecService.triggerForm(reqVO);
        System.out.println(respVO);
    }

}
