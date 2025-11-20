package com.cmsr.onebase.module.etl.build.controller.mgt;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.mgt.ETLWorkflowService;
import com.cmsr.onebase.module.etl.build.service.mgt.vo.*;
import com.cmsr.onebase.module.etl.common.preview.DataPreview;
import com.cmsr.onebase.module.etl.core.vo.WorkflowPageReqVO;
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
    public CommonResult<PageResult<WorkflowBriefVO>> pageQueryFlow(@Validated WorkflowPageReqVO pageReqVO) {
        PageResult<WorkflowBriefVO> workflowPage = workflowService.getWorkflowPage(pageReqVO);
        return CommonResult.success(workflowPage);
    }

    @GetMapping("/{workflowId}")
    public CommonResult<WorkflowDetailVO> queryWorkflowDetailedInfo(@PathVariable("workflowId") @NotNull Long workflowId) {
        WorkflowDetailVO workflowDetail = workflowService.getWorkflowDetail(workflowId);
        return CommonResult.success(workflowDetail);
    }

    @PostMapping("/create")
    public CommonResult<Long> createWorkflow(@Validated @RequestBody WorkflowCreateVO createVO) {
        Long workflow = workflowService.createWorkflow(createVO);
        return CommonResult.success(workflow);
    }

    @PostMapping("/update")
    public CommonResult<Boolean> updateWorkflow(@Validated @RequestBody WorkflowUpdateVO updateVO) {
        workflowService.updateWorkflow(updateVO);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/delete")
    public CommonResult<Boolean> deleteWorkflow(@RequestParam("id") Long id) {
        workflowService.deleteWorkflow(id);
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

    @GetMapping("/schedule/{workflowId}")
    public CommonResult<ScheduleRespVO> getWorkflowSchedule(@PathVariable("workflowId") Long workflowId) {
        ScheduleRespVO scheduleRespVO = workflowService.getWorkflowSchedule(workflowId);
        return CommonResult.success(scheduleRespVO);
    }

    @PostMapping("/schedule")
    public CommonResult<Boolean> configScheduleStrategy(@Validated @RequestBody ScheduleConfigVO scheduleConfigVO) {
        workflowService.configScheduleStrategy(scheduleConfigVO);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/{workflowId}/start")
    public CommonResult<Boolean> startWorkflowManually(@PathVariable("workflowId") Long workflowId) {
        workflowService.startWorkflowManually(workflowId);
        return CommonResult.success(Boolean.TRUE);
    }

    @GetMapping("/{applicationId}/logs")
    public CommonResult<PageResult<ExecutionLogVO>> queryWorkflowExecutionLogs(@PathVariable("applicationId") Long applicationId,
                                                                               @RequestParam("workflowId") Long workflowId,
                                                                               @RequestParam("pageNo") Integer pageNo,
                                                                               @RequestParam("pageSize") Integer pageSize) {
        PageResult<ExecutionLogVO> workflowExecutionLogs = workflowService.getWorkflowExecutionLogs(applicationId, workflowId, pageNo, pageSize);
        return CommonResult.success(workflowExecutionLogs);
    }

    @PostMapping("/preview")
    public CommonResult<DataPreview> previewWorkflow(@RequestBody PreviewReqVO previewReqVO) {
        DataPreview preview = workflowService.previewWorkflow(  previewReqVO);
        return CommonResult.success(preview);
    }

}
