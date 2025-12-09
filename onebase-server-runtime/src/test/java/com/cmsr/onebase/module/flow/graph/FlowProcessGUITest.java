package com.cmsr.onebase.module.flow.graph;

import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.module.flow.api.FlowProcessExecApiImpl;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.flow.FlowRemoteCallExecutor;
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

@Setter
@SpringBootTest(classes = OneBaseServerRuntimeApplication.class)
public class FlowProcessGUITest {


        @Autowired
        private FlowProcessRepository flowProcessRepository;

        @Autowired
        private FlowProcessExecApiImpl flowProcessExecApi;

        @Autowired//日期触发用
        private FlowRemoteCallExecutor flowRemoteCallExecutor;

        @Autowired
        private FlowProcessExecService flowProcessExecService;

//    @Test
//    public void testGUI1() throws IOException {
//        TenantContextHolder.setIgnore(true);
//        FormTriggerReqVO reqVO = new FormTriggerReqVO();
//        reqVO.setProcessId(124225813148991488L);  //
//        reqVO.setExecutionUuid("d0adb4be-a3f7-4283-afff-1fb6f8f9c1f2");
//        Map<Long, Object> inputParams = Map.of(
//
//        );
//        reqVO.setInputParams(inputParams);
//        FormTriggerRespVO respVO = flowProcessExecService.triggerForm(reqVO);
//        System.out.println(respVO);
//    }


//    @Test
//    public void testGUI2() throws IOException {
//        TenantContextHolder.setIgnore(true);
//        FormTriggerReqVO reqVO = new FormTriggerReqVO();
//        reqVO.setProcessId(130142268047982592L);  //
////        reqVO.setExecutionUuid("79858562-e948-4d26-9d0c-dfc181829712");
//        Map<Long, Object> inputParams = Map.of(
////                115291181633175552L,"流字段"
//        );
//        reqVO.setInputParams(inputParams);
//        FormTriggerRespVO respVO = flowProcessExecService.triggerForm(reqVO);
//        System.out.println(respVO);
//    }


////    分支
//    @Test
//    public void testGUI3() throws IOException {
//        TenantContextHolder.setIgnore(true);
//        FormTriggerReqVO reqVO = new FormTriggerReqVO();
//        reqVO.setProcessId(131695087709290496L);  //
////        reqVO.setExecutionUuid("79858562-e948-4d26-9d0c-dfc181829712");
//        Map<Long, Object> inputParams = Map.of(
////                115291181633175552L,"流字段"
//        );
//        reqVO.setInputParams(inputParams);
//        FormTriggerRespVO respVO = flowProcessExecService.triggerForm(reqVO);
//        System.out.println(respVO);
//    }



//    //    分支
//    @Test
//    public void testGUI4() throws IOException {
//        TenantContextHolder.setIgnore(true);
//        FormTriggerReqVO reqVO = new FormTriggerReqVO();
//        reqVO.setProcessId(131695087709290496L);  //
////        reqVO.setExecutionUuid("79858562-e948-4d26-9d0c-dfc181829712");
//        Map<Long, Object> inputParams = Map.of(
//                115291181633175552L,"中文111"
//        );
//        reqVO.setInputParams(inputParams);
//        FormTriggerRespVO respVO = flowProcessExecService.triggerForm(reqVO);
//        System.out.println(respVO);
//    }


}
