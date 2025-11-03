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
        // TODO:
        return CommonResult.success(null);
    }

    @PostMapping("/{workflowId}/start")
    public CommonResult<Boolean> startWorkflowManually(@PathVariable("workflowId") Long workflowId) {
        workflowService.startWorkflowManually(workflowId);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/{workflowId}/enable")
    public CommonResult<Boolean> enableWorkflow(@PathVariable("workflowId") Long workflowId) {
        workflowService.enableWorkflow(workflowId);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/{workflowId}/disable")
    public CommonResult<Boolean> disableWorkflow(@PathVariable("workflowId") Long workflowId) {
        workflowService.disableWorkflow(workflowId);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/{workflowId}/schedule")
    public CommonResult<Boolean> configScheduleStrategy(@PathVariable("workflowId") Long workflowId,
                                                        @Validated @RequestBody ETLScheduleConfigVO scheduleConfigVO) {
        // TODO:
        scheduleConfigVO.setWorkflowId(workflowId);
        workflowService.configScheduleStrategy(scheduleConfigVO);
        return CommonResult.success(Boolean.TRUE);
    }
}
