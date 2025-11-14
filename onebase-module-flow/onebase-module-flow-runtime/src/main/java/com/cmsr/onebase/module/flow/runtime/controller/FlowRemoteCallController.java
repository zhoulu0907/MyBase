package com.cmsr.onebase.module.flow.runtime.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.flow.core.flow.ExecutorResult;
import com.cmsr.onebase.module.flow.core.flow.FlowRemoteCallExecutor;
import com.cmsr.onebase.module.flow.core.flow.RemoteCallRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @PostMapping("/trigger")
    @Operation(summary = "远程调用触发")
    public CommonResult<ExecutorResult> triggerForm(@RequestBody @Validated RemoteCallRequest request) {
        ExecutorResult result = flowRemoteCallExecutor.executeFlow(request);
        return CommonResult.success(result);
    }


}
