package com.cmsr.onebase.module.flow.graph;

import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.module.flow.api.FlowProcessExecApiImpl;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerReqDTO;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerRespDTO;
import com.cmsr.onebase.module.flow.api.dto.TriggerEventEnum;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.flow.ExecutorRequest;
import com.cmsr.onebase.module.flow.core.flow.FlowExecuteProvider;
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

@Setter
@SpringBootTest(classes = OneBaseServerRuntimeApplication.class)
public class FlowProcessEntityTest {


        @Autowired
        private FlowProcessRepository flowProcessRepository;

        @Autowired
        private FlowProcessExecApiImpl flowProcessExecApi;

        @Autowired
        private FlowExecuteProvider flowExecuteProvider;

        @Autowired
        private FlowProcessExecService flowProcessExecService;

        //表单实体测试  前置 创建前 过滤条件 TEXT 字段类型 等于 公式 CONCATENATE("new","123")
    @Test
    public void testEntity1() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
//        System.out.println("reqDTO.getEntityId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_CREATE);
        reqDTO.setFieldData(Map.of(
                "115291181633175552", "new123"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }

    //表单实体测试  前置 修改前 过滤条件 TEXT 字段类型 等于 公式 CONCATENATE("new","123")
    @Test
    public void testEntity2() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getEntityId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_UPDATE);
        reqDTO.setFieldData(Map.of(
                "115291181633175552", "new123"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }
/*
reqDTO.getEntityId()::::::::::::::::::::::::::5bc689c7-fea4-4995-960f-fe2f1e44f1a6
2025-11-04 14:32:27,262 [main] INFO  c.c.o.m.f.a.f.FormulaEngineApiImpl -- ############: CONCATENATE("new","123")
2025-11-04 14:32:37,639 [main] INFO  c.c.o.m.f.a.f.FormulaEngineApiImpl -- 公式执行成功，公式：CONCATENATE("new","123")，结果：new123，耗时：10371ms
2025-11-04 14:32:38,225 [main] INFO  c.y.liteflow.core.FlowExecutor -- [69210f9e802e4ec990004ad61b750368]:requestId has generated
2025-11-04 14:32:38,234 [main] INFO  c.y.liteflow.core.FlowExecutor -- [69210f9e802e4ec990004ad61b750368]:slot[0] offered
2025-11-04 14:32:38,273 [main] INFO  c.c.o.m.f.c.s.StartEntityNodeComponent -- [69210f9e802e4ec990004ad61b750368]:[O]start component[startEntity] execution
2025-11-04 14:32:38,290 [main] INFO  c.c.o.m.f.c.s.StartEntityNodeComponent -- [69210f9e802e4ec990004ad61b750368]:component[startEntity] finished in 7 milliseconds
2025-11-04 14:32:38,291 [main] INFO  c.c.o.m.f.c.sys.EndNodeComponent -- [69210f9e802e4ec990004ad61b750368]:[O]start component[end] execution
2025-11-04 14:32:38,292 [main] INFO  c.c.o.m.f.c.sys.EndNodeComponent -- [69210f9e802e4ec990004ad61b750368]:component[end] finished in 1 milliseconds
2025-11-04 14:32:38,294 [main] INFO  com.yomahub.liteflow.slot.Slot -- [69210f9e802e4ec990004ad61b750368]:CHAIN_NAME[chain119806978978316288]
startEntity<7>==>end<1>
2025-11-04 14:32:38,295 [main] INFO  com.yomahub.liteflow.slot.DataBus -- [69210f9e802e4ec990004ad61b750368]:slot[0] released
2025-11-04 14:32:38,343 [main] INFO  c.c.o.f.b.a.AnyLineDBInfoListener -- injectTenantIdToOneEntity--------------> isTableTenantIgnored: false
2025-11-04 14:32:38,343 [main] INFO  c.c.o.f.b.a.AnyLineDBInfoListener -- anyline global prepareInsert ---------> snow id:121131100220850176
2025-11-04 14:32:38,359 [main] INFO  o.a.data.adapter.DriverAdapter -- [cmd:1762237958349-95919916][thread:1][ds:default][action:select][cmd:
SELECT M.*,pg_catalog.format_type ( FA.ATTTYPID, FA.ATTTYPMOD ) AS FULL_TYPE,FD.DESCRIPTION AS COLUMN_COMMENT FROM information_schema.columns M
LEFT JOIN pg_namespace FN ON M.table_schema = FN.nspname
LEFT JOIN pg_class FC ON FC.RELNAME = M.TABLE_NAME AND FC.relnamespace = FN.oid
LEFT JOIN pg_attribute FA ON FA.ATTNAME = M.COLUMN_NAME AND FA.ATTRELID = FC.OID
LEFT JOIN pg_description FD ON FD.OBJOID = FC.OID AND FD.OBJSUBID = M.ORDINAL_POSITION
WHERE (M.TABLE_CATALOG = ? AND  M.TABLE_SCHEMA = ? AND  M.TABLE_NAME ILIKE ?)
ORDER BY M.TABLE_SCHEMA ASC, M.TABLE_NAME ASC, M.ORDINAL_POSITION ASC
]
[param:
param0=onebase_cloud_v3(java.lang.String)
param1=public(java.lang.String)
param2=flow_execution_log(java.lang.String)
];
2025-11-04 14:32:38,569 [main] INFO  o.a.data.adapter.DriverAdapter -- [cmd:1762237958349-95919916][thread:1][ds:default][action:select][执行耗时:00:00:00.209]
2025-11-04 14:32:38,572 [main] INFO  o.a.data.adapter.DriverAdapter -- [cmd:1762237958349-95919916][thread:1][ds:default][action:select][封装耗时:00:00:00.000][封装行数:19]
2025-11-04 14:32:38,581 [main] INFO  o.a.data.adapter.DriverAdapter -- [cmd:1762237958349-95919916][thread:1][ds:default][columns][catalog:CATALOG:onebase_cloud_v3][schema:SCHEMA:public][table:TABLE:flow_execution_log][total:19][根据metadata解析:0][根据系统表查询:19][根据驱动内置接口补充:0][执行耗时:00:00:00.232]
2025-11-04 14:32:38,582 [main] INFO  o.a.data.adapter.DriverAdapter -- [cmd:1762237958349-95919916][thread:1][ds:default][columns][catalog:CATALOG:onebase_cloud_v3][schema:SCHEMA:public][table:TABLE:flow_execution_log][total:19][根据metadata解析:0][根据系统表查询:19][根据根据驱动内置接口补充:0][执行耗时:00:00:00.232]
2025-11-04 14:32:38,582 [main] INFO  o.a.data.adapter.DriverAdapter -- [check column metadata][origin:14][result:14]
2025-11-04 14:32:38,585 [main] INFO  o.a.data.adapter.DriverAdapter -- [cmd:1762237958342-25095262][thread:1][ds:default][action:insert][cmd:
INSERT INTO flow_execution_log(trace_id, execution_uuid, application_id, process_id, start_time, end_time, duration_time, execution_result, log_text, error_message, id, create_time, update_time, deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
]
[param:
trace_id=5bc689c7-fea4-4995-960f-fe2f1e44f1a6(java.lang.String)
execution_uuid=481f3843-5d8e-427a-9f21-dd12d2e75b1f(java.lang.String)
application_id=92905794847703040(java.lang.Long)
process_id=119806978978316288(java.lang.Long)
start_time=2025-11-04 14:32:38.126(java.sql.Timestamp)
end_time=2025-11-04 14:32:38.317(java.sql.Timestamp)
duration_time=191(java.lang.Long)
execution_result=success(java.lang.String)
log_text=[0] 流程执行开始
[155] 表达实体触发开始执行
[166] 结束节点开始执行
[191] 流程执行结束(java.lang.String)
error_message=null
id=121131100220850176(java.lang.Long)
create_time=2025-11-04 14:32:38.344(java.sql.Timestamp)
update_time=2025-11-04 14:32:38.344(java.sql.Timestamp)
deleted=0(java.lang.Long)
];
2025-11-04 14:32:38,716 [main] INFO  o.a.data.adapter.DriverAdapter -- [cmd:1762237958342-25095262][thread:1][ds:default][action:insert][table:TABLE:flow_execution_log][执行耗时:00:00:00.131][影响行数:1]
respDTO===========================================EntityTriggerRespDTO(traceId=5bc689c7-fea4-4995-960f-fe2f1e44f1a6, processId=119806978978316288, success=true, triggered=true, code=null, message=null, detail=null, cause=null, executionEnd=true)
*/

    //表单实体测试  前置 删除前 过滤条件 TEXT 字段类型 等于 公式 CONCATENATE("new","123")
    @Test
    public void testEntity3() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getEntityId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        reqDTO.setFieldData(Map.of(
                "115291181633175552", "new123"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }
//respDTO===========================================EntityTriggerRespDTO(traceId=d53403f0-3ded-4cad-8a63-6778babc300f, processId=119806978978316288, success=true, triggered=true, code=null, message=null, detail=null, cause=null, executionEnd=true)

    //表单实体测试  前置 删除前 过滤条件 TEXT 字段类型 等于 公式 LEFT("OneBase", 3)=One
    @Test
    public void testEntity4() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        reqDTO.setFieldData(Map.of(
                "115291181633175552", "One"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }
//respDTO===========================================EntityTriggerRespDTO(traceId=d53403f0-3ded-4cad-8a63-6778babc300f, processId=119806978978316288, success=true, triggered=true, code=null, message=null, detail=null, cause=null, executionEnd=true)

    //表单实体测试  前置 删除前 过滤条件 TEXT 字段类型 等于 公式 LOWER("ABC")=abc
    @Test
    public void testEntity5() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        reqDTO.setFieldData(Map.of(
                "115291181633175552", "abc"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }
//respDTO===========================================EntityTriggerRespDTO(traceId=9137d34f-6801-457c-94d5-4dface861296, processId=119806978978316288, success=true, triggered=true, code=null, message=null, detail=null, cause=null, executionEnd=true)

    //表单实体测试  前置 删除前 过滤条件 TEXT 字段类型 等于 公式 LEN("OneBase")=7
    @Test
    public void testEntity6() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        reqDTO.setFieldData(Map.of(
                "115291181633175552", "8"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }


    //表单实体测试  前置 删除前 过滤条件 TEXT 字段类型 等于 公式 TRIM(" abc abc ")=abc abc
    @Test
    public void testEntity7() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        reqDTO.setFieldData(Map.of(
                "115291181633175552", "abc abc"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }



    //表单实体测试  前置 删除前 过滤条件 TEXT 字段类型 等于 公式 MID("OneBase", 2, 3)=neB
    @Test
    public void testEntity8() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        reqDTO.setFieldData(Map.of(
                "115291181633175552", "neB"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }

    //表单实体测试  前置 删除前 过滤条件 TEXT 字段类型 不等于 静态常量 1 2 三 si
                                               //    静态常量 2
                                               //    静态常量 三
                                               //    静态常量 si
    @Test
    public void testEntity9() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        reqDTO.setFieldData(Map.of(
                "115291181633175552", "九"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }

    //表单实体测试  前置 删除前 过滤条件 TEXT 字段类型
    //   多个 且
    //    包含
    //    静态常量 1
    //    静态常量 2
    //    静态常量 三
    //    静态常量 si
    @Test
    public void testEntity10() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        reqDTO.setFieldData(Map.of(
                "115291181633175552", "12三si"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }

    //表单实体测试  前置 删除前 过滤条件 TEXT 字段类型
    //   多个 且
    //    包含
    //    静态常量 1
    //    静态常量 2
    //    静态常量 三
    //    静态常量 si
    @Test
    public void testEntity11() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        reqDTO.setFieldData(Map.of(
                "115291181633175552", "oob三bb2pp三sdfsidd2dd"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }


    //表单实体测试  前置 删除前 过滤条件 TEXT 字段类型
    //   多个 且
    //    不包含 静态常量 1
    //    包含   静态常量 2
    //    包含   静态常量 三
    //    包含   静态常量 si
    @Test
    public void testEntity12() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        reqDTO.setFieldData(Map.of(
                "115291181633175552", "oobbb2pp三sdfsidd1dd"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }


    //表单实体测试  前置 删除前 过滤条件 TEXT 字段类型
    //   多个 存在于
    //    存在于
    //    静态常量 1122
    //    静态常量 111222
    //    静态常量 122
    //    静态常量 11111222222
    @Test
    public void testEntity13() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        reqDTO.setFieldData(Map.of(
                "115291181633175552", "212"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }


    //表单实体测试  前置 删除前 过滤条件 TEXT 字段类型
    //   单个 存在于
    //    存在于
    //    静态常量 存存存存
    @Test
    public void testEntity14() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        reqDTO.setFieldData(Map.of(
                "115291181633175552", "三on"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }


    //表单实体测试  前置 删除前 过滤条件 TEXT 字段类型
    //   多个 不存在于
    //    不存在于
    //    静态常量 111
    //    静态常量 222
    //    静态常量 三三三
    //    静态常量 fourfour
    @Test
    public void testEntity15() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        reqDTO.setFieldData(Map.of(
                "115291181633175552", "6"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }


    //表单实体测试  前置 删除前 过滤条件 TEXT 字段类型
    //    字段为空
    @Test
    public void testEntity16() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        reqDTO.setFieldData(Map.of(
                "115291181633175552", ""
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }


    //表单实体测试  前置 删除前 过滤条件 TEXT 字段类型
    //    字段不为空
    @Test
    public void testEntity17() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        reqDTO.setFieldData(Map.of(
                "115291181633175552", "部位空空空"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }



/////////////////////// 或 //////////////////////////////////////////////

//表单实体测试  前置 删除前 过滤条件 TEXT 字段类型
//    多个或
//    等于 测试
//    等于 888
@Test
public void testEntity18() throws IOException {
    EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
    reqDTO.setTraceId(UUID.randomUUID().toString());
    System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
    reqDTO.setEntityId(112241703294926848L);
    reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
    reqDTO.setFieldData(Map.of(
//            "115291181633175552", "888",
            "115291181633175552", "测9999试"
    ));
    EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
    System.out.println("respDTO==========================================="+respDTO);
}


    //表单实体测试  前置 删除前 过滤条件 TEXT 字段类型
//    多个或
//    等于 测试
//    等于 888
//    等于 nnn
    @Test
    public void testEntity19() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        reqDTO.setFieldData(Map.of(
//            "115291181633175552", "888",
                "115291181633175552", "888"
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }



    //表单实体测试  前置 删除前 过滤条件 数字 字段类型 121313069396492288 数字流
//   且
//   范围 111 999
    @Test
    public void testEntity20() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        reqDTO.setFieldData(Map.of(
//            "115291181633175552", "888",
                "121313069396492288", 123
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }


    //表单实体测试  后置 创建后 过滤条件 数字 字段类型 121313069396492288 数字流
//   且
//   等于 -101
    @Test
    public void testEntity21() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_CREATE);
        reqDTO.setFieldData(Map.of(
//            "115291181633175552", "888",
                "121313069396492288", -102
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }


    //表单实体测试  后置 创建后 过滤条件 数字 字段类型 121313069396492288 数字流 公式
//   且
//   RANDOMBETWEEN(1,10) 随机1到10
    @Test
    public void testEntity22() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_CREATE);
        reqDTO.setFieldData(Map.of(
//            "115291181633175552", "888",
                "121313069396492288", 10
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }


    //表单实体测试  后置 创建后 过滤条件 数字 字段类型 121313069396492288 数字流 公式
//   且
//   SQRT(9)  平方根 3------9
    @Test
    public void testEntity23() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_CREATE);
        reqDTO.setFieldData(Map.of(
//            "115291181633175552", "888",
                "121313069396492288", 3
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }

//reqDTO.getTraceId()::::::::::::::::::::::::::b069e6e7-6f35-4fb2-8fe4-952adb4006f8
//2025-11-05 14:04:37,795 [main] INFO  c.c.o.m.f.a.f.FormulaEngineApiImpl -- ############: SQRT(9)
//2025-11-05 14:04:38,316 [pool-6-thread-1] INFO  c.c.o.f.b.c.BannerApplicationRunner --
//            ----------------------------------------------------------
//    OneBase3 启动成功！
//            ----------------------------------------------------------
//            2025-11-05 14:04:48,249 [main] INFO  c.c.o.m.f.a.f.FormulaEngineApiImpl -- 公式执行成功，公式：SQRT(9)，结果：3，耗时：10449ms
//    respDTO===========================================EntityTriggerRespDTO(traceId=b069e6e7-6f35-4fb2-8fe4-952adb4006f8, processId=119806978978316288, success=true, triggered=false, code=null, message=触发条件不匹配: OrExpression(andExpressions=[AndExpression(expressionItems=[ExpressionItem(key=121313069396492288, op=NOT_EQUALS, value=3, operatorType=FORMULA, fieldType=NUMBER, jdbcType=NUMERIC)])]), detail=null, cause=null, executionEnd=false)


    //表单实体测试  后置 创建后 过滤条件 数字 字段类型 121313069396492288 数字流 公式
//   且
//   COUNT(小明,小王,小张,小李) 4  报错 小明未定义
//   COUNT("小明","小王","小张","小李")  传入4 触发成功 公式执行成功，公式：COUNT("小明","小王","小张","小李")，结果：0，耗时：12171ms 这个结果应该不对
    @Test
    public void testEntity24() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_CREATE);
        reqDTO.setFieldData(Map.of(
//            "115291181633175552", "888",
                "121313069396492288", 0
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }


    //表单实体测试  后置 创建后 过滤条件 数字 字段类型 121313069396492288 数字流 公式
//   且
//   POWER(3，2) 9
    @Test
    public void testEntity25() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_CREATE);
        reqDTO.setFieldData(Map.of(
//            "115291181633175552", "888",
                "121313069396492288", 9
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }


    //表单实体测试  后置 创建后 过滤条件 数字 字段类型 121313069396492288 数字流 公式
//   且
//   大于 666
    @Test
    public void testEntity26() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_CREATE);
        reqDTO.setFieldData(Map.of(
//            "115291181633175552", "888",
                "121313069396492288", 667
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }


    //表单实体测试  后置 创建后 过滤条件 数字 字段类型 121313069396492288 数字流 公式
//   且
//   大于等于 88
    @Test
    public void testEntity27() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_CREATE);
        reqDTO.setFieldData(Map.of(
//            "115291181633175552", "888",
                "121313069396492288", 87
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }


    //表单实体测试  后置 创建后 过滤条件 数字 字段类型 121313069396492288 数字流 公式
//   且
//   小于等于 99
    @Test
    public void testEntity28() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_CREATE);
        reqDTO.setFieldData(Map.of(
//            "115291181633175552", "888",
                "121313069396492288", 99
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }


    //表单实体测试  后置 创建后 过滤条件 数字 字段类型 121313069396492288 数字流 公式
//   且
//   小于等于 99
//    大于等于 -200
    @Test
    public void testEntity29() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_CREATE);
        reqDTO.setFieldData(Map.of(
//            "115291181633175552", "888",
                "121313069396492288", -201
        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }


    //表单实体测试  后置 创建后 过滤条件  字段类型  时间 测流时间 119596817636163584
//   且
//   范围  静态 时间范围 2025-11-05  - 2025-11-07

    @Test
    public void testEntity30() throws IOException {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        System.out.println("reqDTO.getTraceId()::::::::::::::::::::::::::"+reqDTO.getTraceId());
        reqDTO.setEntityId(112241703294926848L);
        reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_CREATE);
        reqDTO.setFieldData(Map.of(
                "119596817636163584", LocalDate.parse("2025-11-06")
//                "119596817636163584", "2025-11-06"

        ));
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        System.out.println("respDTO==========================================="+respDTO);
    }



}
