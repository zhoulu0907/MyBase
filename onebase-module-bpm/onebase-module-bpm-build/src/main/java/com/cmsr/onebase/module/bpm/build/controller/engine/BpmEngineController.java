package com.cmsr.onebase.module.bpm.build.controller.engine;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.bpm.build.controller.engine.vo.BpmExecuteReqVO;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.repository.FlowDefinitionRepository;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.entity.WfFlowDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.service.DefService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 审批流控制器
 *
 * @author matianyu
 * @date 2025-09-17
 */
@Tag(name = "管理后台 - 审批流")
@RestController
@RequestMapping("/bpm/engine")
@Validated
@Slf4j
public class BpmEngineController {
    @Resource
    private FlowDefinitionRepository flowDefinitionRepository;

    @Resource
    private DefService defService;

    private String testJson = "{\n" +
            "  \"flowCode\": \"leaveFlow-parallel2\",\n" +
            "  \"flowName\": \"并行-分开\",\n" +
            "  \"formCustom\": \"N\",\n" +
            "  \"formPath\": \"system/leave/approve\",\n" +
            "  \"version\": \"1\",\n" +
            "  \"nodeList\": [\n" +
            "    {\n" +
            "      \"coordinate\": \"140,220|140,220\",\n" +
            "      \"nodeCode\": \"1\",\n" +
            "      \"nodeName\": \"开始\",\n" +
            "      \"nodeRatio\": 0.000,\n" +
            "      \"nodeType\": 0,\n" +
            "      \"skipList\": [\n" +
            "        {\n" +
            "          \"coordinate\": \"160,220;250,220\",\n" +
            "          \"nextNodeCode\": \"2\",\n" +
            "          \"nowNodeCode\": \"1\",\n" +
            "          \"skipType\": \"PASS\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"coordinate\": \"300,220|300,220\",\n" +
            "      \"nodeCode\": \"2\",\n" +
            "      \"nodeName\": \"待提交\",\n" +
            "      \"nodeRatio\": 0.000,\n" +
            "      \"nodeType\": 1,\n" +
            "      \"permissionFlag\": \"role:1@@role:3\",\n" +
            "      \"skipList\": [\n" +
            "        {\n" +
            "          \"coordinate\": \"350,220;450,220\",\n" +
            "          \"nextNodeCode\": \"3\",\n" +
            "          \"nowNodeCode\": \"2\",\n" +
            "          \"skipType\": \"PASS\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"coordinate\": \"500,220|500,220\",\n" +
            "      \"nodeCode\": \"3\",\n" +
            "      \"nodeName\": \"小组长审批\",\n" +
            "      \"nodeRatio\": 0.000,\n" +
            "      \"nodeType\": 1,\n" +
            "      \"permissionFlag\": \"role:1@@role:3\",\n" +
            "      \"skipList\": [\n" +
            "        {\n" +
            "          \"coordinate\": \"550,220;635,220\",\n" +
            "          \"nextNodeCode\": \"4\",\n" +
            "          \"nowNodeCode\": \"3\",\n" +
            "          \"skipType\": \"PASS\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"coordinate\": \"660,220\",\n" +
            "      \"nodeCode\": \"4\",\n" +
            "      \"nodeRatio\": 0.000,\n" +
            "      \"nodeType\": 4,\n" +
            "      \"skipList\": [\n" +
            "        {\n" +
            "          \"coordinate\": \"685,220;716,220;716,120;770,120\",\n" +
            "          \"nextNodeCode\": \"5\",\n" +
            "          \"nowNodeCode\": \"4\",\n" +
            "          \"skipType\": \"PASS\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"coordinate\": \"685,220;715,220;715,280;770,280\",\n" +
            "          \"nextNodeCode\": \"7\",\n" +
            "          \"nowNodeCode\": \"4\",\n" +
            "          \"skipType\": \"PASS\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"coordinate\": \"820,120|820,120\",\n" +
            "      \"nodeCode\": \"5\",\n" +
            "      \"nodeName\": \"大组长审批\",\n" +
            "      \"nodeRatio\": 0.000,\n" +
            "      \"nodeType\": 1,\n" +
            "      \"permissionFlag\": \"role:1@@role:3\",\n" +
            "      \"skipList\": [\n" +
            "        {\n" +
            "          \"coordinate\": \"870,120;990,120\",\n" +
            "          \"nextNodeCode\": \"6\",\n" +
            "          \"nowNodeCode\": \"5\",\n" +
            "          \"skipType\": \"PASS\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"coordinate\": \"1040,120|1040,120\",\n" +
            "      \"nodeCode\": \"6\",\n" +
            "      \"nodeName\": \"部门经理审批\",\n" +
            "      \"nodeRatio\": 0.000,\n" +
            "      \"nodeType\": 1,\n" +
            "      \"permissionFlag\": \"role:1@@role:3\",\n" +
            "      \"skipList\": [\n" +
            "        {\n" +
            "          \"coordinate\": \"1090,120;1220,120\",\n" +
            "          \"nextNodeCode\": \"8\",\n" +
            "          \"nowNodeCode\": \"6\",\n" +
            "          \"skipType\": \"PASS\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"coordinate\": \"820,280|820,280\",\n" +
            "      \"nodeCode\": \"7\",\n" +
            "      \"nodeName\": \"董事长审批\",\n" +
            "      \"nodeRatio\": 0.000,\n" +
            "      \"nodeType\": 1,\n" +
            "      \"permissionFlag\": \"role:1@@role:3\",\n" +
            "      \"skipList\": [\n" +
            "        {\n" +
            "          \"coordinate\": \"870,280;1220,280\",\n" +
            "          \"nextNodeCode\": \"9\",\n" +
            "          \"nowNodeCode\": \"7\",\n" +
            "          \"skipType\": \"PASS\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"coordinate\": \"820,320;820,350;500,350;500,260\",\n" +
            "          \"nextNodeCode\": \"3\",\n" +
            "          \"nowNodeCode\": \"7\",\n" +
            "          \"skipType\": \"REJECT\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"coordinate\": \"1240,120|1240,120\",\n" +
            "      \"nodeCode\": \"8\",\n" +
            "      \"nodeName\": \"结束1\",\n" +
            "      \"nodeRatio\": 0.000,\n" +
            "      \"nodeType\": 2,\n" +
            "      \"skipList\": [\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"coordinate\": \"1240,280|1240,280\",\n" +
            "      \"nodeCode\": \"9\",\n" +
            "      \"nodeName\": \"结束2\",\n" +
            "      \"nodeRatio\": 0.000,\n" +
            "      \"nodeType\": 2,\n" +
            "      \"skipList\": [\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}\n";

    @PostMapping("/execute")
    @Operation(summary = "执行流程")
    @PreAuthorize("@ss.hasPermission('bpm:engine:execute')")
    public CommonResult<Boolean> execute(@Valid @RequestBody BpmExecuteReqVO reqVO) {
        long startTime = System.currentTimeMillis();
        // Object result = bpmEngineService.executeFormulaWithParams(reqVO.getFormula(), reqVO.getParameters());
        long executionTime = System.currentTimeMillis() - startTime;
        log.info("流程执行成功，流程ID：{}，结果：{}，耗时：{}ms", reqVO.getProcessId(), true, executionTime);
        return CommonResult.success(true);
    }

    @GetMapping("/test")
    @Operation(summary = "执行流程2")
    //@PreAuthorize("@ss.hasPermission('bpm:engine:execute')")
    public CommonResult<Boolean> execute2() {
        long count = flowDefinitionRepository.count();
        System.out.println(count);

        WfFlowDefinition copy = new WfFlowDefinition();
        copy.setCreateTime(new Date());

        System.out.println(copy.getCreateTime());
        System.out.println(((Definition) copy).getCreateTime());


        defService.importJson(testJson);

        Definition definition = defService.getById("1");
        System.out.println(definition);
        return CommonResult.success(true);
    }
}
