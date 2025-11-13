package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.flow.core.graph.FlowCacheClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 流程管理 - 管理员控制器
 */
@Setter
@RestController
@RequestMapping("/flow/exec")
@Tag(name = "流程管理", description = "流程管理相关接口")
@Validated
public class FlowProcessExecController {

    @Autowired
    private FlowCacheClient flowCacheClient;


    @PostMapping("/flow-handler/update")
    @Operation(summary = "更新流程")
    public CommonResult<Boolean> updateProcess(@RequestParam("applicationId") Long applicationId) {
        flowCacheClient.applicationUpdate(applicationId);
        return CommonResult.success(Boolean.TRUE);
    }

}
