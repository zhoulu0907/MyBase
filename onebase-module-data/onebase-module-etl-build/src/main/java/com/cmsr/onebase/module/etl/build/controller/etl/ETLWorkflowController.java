package com.cmsr.onebase.module.etl.build.controller.etl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.etl.ETLWorkflowService;
import com.cmsr.onebase.module.etl.build.service.etl.vo.*;
import com.cmsr.onebase.module.etl.core.vo.etl.ETLWorkflowPageReqVO;
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
    private ETLWorkflowService workflowService;

    @GetMapping("/page")
    @Operation(summary = "分页查询数据流")
    public CommonResult<PageResult<ETLWorkflowBriefVO>> pageQueryFlow(@Validated ETLWorkflowPageReqVO pageReqVO) {
        PageResult<ETLWorkflowBriefVO> workflowPage = workflowService.getWorkflowPage(pageReqVO);
        return CommonResult.success(workflowPage);
    }

    @GetMapping("/{workflowId}")
    public CommonResult<ETLWorkflowDetailVO> queryWorkflowDetailedInfo(@PathVariable("workflowId") @NotNull Long workflowId) {
        ETLWorkflowDetailVO workflowDetail = workflowService.getWorkflowDetail(workflowId);
        return CommonResult.success(workflowDetail);
    }

    @PostMapping("/create")
    public CommonResult<Long> createWorkflow(@Validated @RequestBody ETLWorkflowCreateVO createVO) {
        Long workflow = workflowService.createWorkflow(createVO);
        return CommonResult.success(workflow);
    }

    @PostMapping("/update")
    public CommonResult<Boolean> updateWorkflow(@Validated @RequestBody ETLWorkflowUpdateVO updateVO) {
        workflowService.updateWorkflow(updateVO);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/delete")
    public CommonResult<Boolean> deleteWorkflow(@RequestParam("id") Long id) {
        workflowService.deleteWorkflow(id);
        return CommonResult.success(Boolean.TRUE);
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
