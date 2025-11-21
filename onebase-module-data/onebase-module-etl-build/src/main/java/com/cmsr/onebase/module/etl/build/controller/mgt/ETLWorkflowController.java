package com.cmsr.onebase.module.etl.build.controller.mgt;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.mgt.ETLWorkflowService;
import com.cmsr.onebase.module.etl.build.vo.mgt.*;
import com.cmsr.onebase.module.etl.common.preview.ColumnDefine;
import com.cmsr.onebase.module.etl.common.preview.DataPreview;
import com.cmsr.onebase.module.etl.core.vo.WorkflowBriefVO;
import com.cmsr.onebase.module.etl.core.vo.WorkflowPageReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "数据工厂 - ETL管理")
@RestController
@RequestMapping("/etl/workflow")
@Validated
public class ETLWorkflowController {

    @Resource
    private ETLWorkflowService workflowService;

    @Operation(summary = "分页查询ETL")
    @GetMapping("/page")
    public CommonResult<PageResult<WorkflowBriefVO>> pageQueryFlow(@Validated WorkflowPageReqVO pageReqVO) {
        PageResult<WorkflowBriefVO> workflowPage = workflowService.getWorkflowPage(pageReqVO);
        return CommonResult.success(workflowPage);
    }

    @Operation(summary = "查询ETL详细配置信息")
    @GetMapping("/{workflowId}")
    public CommonResult<WorkflowDetailVO> queryWorkflowDetailedInfo(@PathVariable("workflowId") @NotNull Long workflowId) {
        WorkflowDetailVO workflowDetail = workflowService.getWorkflowDetail(workflowId);
        return CommonResult.success(workflowDetail);
    }

    @Operation(summary = "创建ETL配置")
    @PostMapping("/create")
    public CommonResult<Long> createWorkflow(@Validated @RequestBody WorkflowCreateVO createVO) {
        Long workflow = workflowService.createWorkflow(createVO);
        return CommonResult.success(workflow);
    }

    @Operation(summary = "更新ETL配置")
    @PostMapping("/update")
    public CommonResult<Boolean> updateWorkflow(@Validated @RequestBody WorkflowUpdateVO updateVO) {
        workflowService.updateWorkflow(updateVO);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "删除ETL配置")
    @PostMapping("/delete")
    public CommonResult<Boolean> deleteWorkflow(@RequestParam("id") Long id) {
        workflowService.deleteWorkflow(id);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "启用ETL配置")
    @PostMapping("/{workflowId}/enable")
    public CommonResult<Boolean> enableWorkflow(@PathVariable("workflowId") Long workflowId) {
        workflowService.enableWorkflow(workflowId);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "停用ETL配置")
    @PostMapping("/{workflowId}/disable")
    public CommonResult<Boolean> disableWorkflow(@PathVariable("workflowId") Long workflowId) {
        workflowService.disableWorkflow(workflowId);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "获取ETL调度配置")
    @GetMapping("/schedule/{workflowId}")
    public CommonResult<ScheduleRespVO> getWorkflowSchedule(@PathVariable("workflowId") Long workflowId) {
        ScheduleRespVO scheduleRespVO = workflowService.getWorkflowSchedule(workflowId);
        return CommonResult.success(scheduleRespVO);
    }

    @Operation(summary = "编辑ETL调度配置")
    @PostMapping("/schedule")
    public CommonResult<Boolean> configScheduleStrategy(@Validated @RequestBody ScheduleConfigVO scheduleConfigVO) {
        workflowService.configScheduleStrategy(scheduleConfigVO);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "手动执行ETL")
    @PostMapping("/{workflowId}/start")
    public CommonResult<Boolean> startWorkflowManually(@PathVariable("workflowId") Long workflowId) {
        workflowService.startWorkflowManually(workflowId);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "查询ETL日志信息")
    @GetMapping("/{applicationId}/logs")
    public CommonResult<PageResult<ExecutionLogVO>> queryWorkflowExecutionLogs(@PathVariable("applicationId") Long applicationId,
                                                                               @RequestParam("workflowId") Long workflowId,
                                                                               @RequestParam("pageNo") Integer pageNo,
                                                                               @RequestParam("pageSize") Integer pageSize) {
        PageResult<ExecutionLogVO> workflowExecutionLogs = workflowService.getWorkflowExecutionLogs(applicationId, workflowId, pageNo, pageSize);
        return CommonResult.success(workflowExecutionLogs);
    }

    @Operation(summary = "预览ETL节点数据")
    @PostMapping("/preview")
    public CommonResult<DataPreview> previewWorkflow(@RequestBody PreviewReqVO previewReqVO) {
        DataPreview preview = workflowService.previewWorkflow(previewReqVO);
        return CommonResult.success(preview);
    }

    @Operation(summary = "解析工作流SQL节点输出的列信息")
    @PostMapping("/columns")
    public CommonResult<List<ColumnDefine>> queryWorkflowColumns(@RequestBody PreviewReqVO previewReqVO) {
        List<ColumnDefine> columns = workflowService.nodeColumns(previewReqVO);
        return CommonResult.success(columns);
    }

}
