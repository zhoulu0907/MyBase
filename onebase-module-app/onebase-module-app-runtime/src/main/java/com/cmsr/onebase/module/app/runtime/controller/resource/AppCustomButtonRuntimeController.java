package com.cmsr.onebase.module.app.runtime.controller.resource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.runtime.service.custombutton.AppCustomButtonRuntimeService;
import com.cmsr.onebase.module.app.runtime.vo.custombutton.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "应用资源管理-自定义按钮运行态")
@RestController
@RequestMapping("/app/custom-button-runtime")
@Validated
public class AppCustomButtonRuntimeController {

    @Resource
    private AppCustomButtonRuntimeService runtimeService;

    @PostMapping("/list-available")
    @Operation(summary = "查询运行态可用按钮")
    public CommonResult<List<RuntimeCustomButtonRespVO>> listAvailable(@Valid @RequestBody RuntimeCustomButtonListReqVO reqVO) {
        return CommonResult.success(runtimeService.listAvailable(reqVO));
    }

    @PostMapping("/execute")
    @Operation(summary = "执行单条自定义按钮")
    public CommonResult<RuntimeCustomButtonExecuteRespVO> execute(@Valid @RequestBody RuntimeCustomButtonExecuteReqVO reqVO) {
        return CommonResult.success(runtimeService.execute(reqVO));
    }

    @PostMapping("/batch-execute")
    @Operation(summary = "批量执行自定义按钮")
    public CommonResult<RuntimeCustomButtonBatchExecuteRespVO> batchExecute(@Valid @RequestBody RuntimeCustomButtonBatchExecuteReqVO reqVO) {
        return CommonResult.success(runtimeService.batchExecute(reqVO));
    }

    @GetMapping("/exec-log")
    @Operation(summary = "查询执行日志详情")
    public CommonResult<RuntimeCustomButtonExecLogRespVO> getExecLog(@RequestParam("execLogId") Long execLogId) {
        return CommonResult.success(runtimeService.getExecLog(execLogId));
    }
}
