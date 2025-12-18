package com.cmsr.onebase.module.flow.runtime.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.flow.ExecutorResult;
import com.cmsr.onebase.module.flow.core.flow.FlowRemoteCallExecutor;
import com.cmsr.onebase.module.flow.core.flow.FlowRemoteCallRequest;
import com.cmsr.onebase.module.flow.core.graph.FlowProcessCache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程执行 - 执行控制器
 */
@Setter
@RestController
@RequestMapping("/flow/remote-call/")
@Tag(name = "流程远程调用", description = "流程远程调用接口")
@Validated
public class FlowRemoteCallController {

    @Autowired
    private FlowRemoteCallExecutor flowRemoteCallExecutor;

    private FlowProcessCache flowProcessCache = FlowProcessCache.getInstance();

    @PostMapping("/trigger")
    @Operation(summary = "远程调用触发")
    public CommonResult<ExecutorResult> triggerForm(@RequestBody @Validated FlowRemoteCallRequest request) {
        ExecutorResult result = flowRemoteCallExecutor.executeFlow(request);
        return CommonResult.success(result);
    }

    @GetMapping("/query")
    @Operation(summary = "查询流程")
    public CommonResult<List<FlowProcessDO>> getProcess() {
        List<FlowProcessDO> allProcess = flowProcessCache.getAllProcess();
        return CommonResult.success(allProcess);
    }
}
