package com.cmsr.onebase.module.etl.build.controller.etl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.etl.ETLWorkflowService;
import com.cmsr.onebase.module.etl.build.service.etl.vo.ETLPageReqVO;
import com.cmsr.onebase.module.etl.build.service.etl.vo.ETLWorkflowBriefVO;
import com.cmsr.onebase.module.etl.build.service.etl.vo.ETLWorkflowDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ETL - 数据流管理")
@RestController
@RequestMapping("/etl/workflow")
@Validated
public class ETLWorkflowController {

    @Resource
    private ETLWorkflowService etlWorkflowService;

    @GetMapping("/page")
    @Operation(summary = "分页查询数据流")
    public CommonResult<PageResult<ETLWorkflowBriefVO>> pageQueryFlow(@Validated ETLPageReqVO pageReqVO) {
        return CommonResult.success(null);
    }

    @GetMapping("/{workflowId}")
    public CommonResult<ETLWorkflowDetailVO> queryWorkflowDetailedInfo(@PathVariable("workflowId") @NotNull Long workflowId) {
        return CommonResult.success(null);
    }

    @PostMapping("/create")
    public CommonResult<Long> createWorkflow() {
        return CommonResult.success(null);
    }

    @PostMapping("/update")
    public CommonResult<Boolean> updateWorkflow() {
        return CommonResult.success(null);
    }

    @PostMapping("/delete")
    public CommonResult<Boolean> deleteWorkflow() {
        return CommonResult.success(null);
    }

    @GetMapping("/logs")
    public CommonResult<PageResult<Object>> queryWorkflowExecutionLogs() {
        return CommonResult.success(null);
    }

    @PostMapping("/start")
    public CommonResult<Boolean> startWorkflowManually() {
        return CommonResult.success(null);
    }

    @PostMapping("/enable")
    public CommonResult<Boolean> enableWorkflow() {
        return CommonResult.success(null);
    }

    @PostMapping("/disable")
    public CommonResult<Boolean> disableWorkflow() {
        return CommonResult.success(null);
    }

    @PostMapping("/schedule")
    public CommonResult<Boolean> configScheduleStrategy() {
        return CommonResult.success(null);
    }
}
