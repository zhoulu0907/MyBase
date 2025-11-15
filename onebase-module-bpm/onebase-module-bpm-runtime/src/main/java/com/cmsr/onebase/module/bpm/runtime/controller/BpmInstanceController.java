package com.cmsr.onebase.module.bpm.runtime.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.bpm.runtime.service.BpmInstanceService;
import com.cmsr.onebase.module.bpm.runtime.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 流程实例管理Controller
 *
 * @author liyang
 * @date 2025-10-21
 */
@Tag(name = "流程实例管理")
@RestController
@RequestMapping("/bpm/instance")
@Validated
@Slf4j
public class BpmInstanceController {

    @Resource
    private BpmInstanceService bpmExecService;

    @PostMapping("/submit")
    @Operation(summary = "流程发起")
    public CommonResult<BpmSubmitRespVO> exec(@RequestBody @Valid BpmSubmitReqVO reqVO) {
        log.info("执行流程实例的操作按钮: {}", reqVO);
        BpmSubmitRespVO respVO = bpmExecService.submit(reqVO);
        return CommonResult.success(respVO);
    }

    @PostMapping("/exec-task")
    public CommonResult<Boolean> exec(@RequestBody @Valid ExecTaskReqVO reqVO) {
        log.info("执行流程实例的操作按钮: {}", reqVO);
        bpmExecService.execTask(reqVO);
        return CommonResult.success(true);
    }

    @GetMapping("/get-operator-record")
    @Operation(summary = "获取流程实例的操作记录")
    public CommonResult<List<BpmOperatorRecordRespVO.OperatorRecord>> getOperatorRecord(@RequestParam("instanceId") Long instanceId) {
        log.info("获取流程实例的操作记录: {}", instanceId);
        List<BpmOperatorRecordRespVO.OperatorRecord> records = bpmExecService.getOperatorRecord(instanceId);
        return CommonResult.success(records);
    }

    @GetMapping("/get-form-detail")
    public CommonResult<BpmTaskDetailRespVO> getFormDetail(@Valid BpmTaskDetailReqVO reqVO) {
        log.info("获取流程详情: {}", reqVO);
        return CommonResult.success(bpmExecService.getFormDetail(reqVO));
    }

    /**
     * 流程预测
     *
     * @param reqVO
     */
    @PostMapping("/flow-predict")
    @Operation(summary = "流程预测")
    public CommonResult<List<BpmPredictRespVO.NodeInfo>> flowPredict(@RequestBody @Valid BpmPredictReqVO reqVO) {
        log.info("流程预测: {}", reqVO);
        return CommonResult.success(bpmExecService.flowPredict(reqVO)) ;
    }

    /**
     * 流程预览
     *
     * @param reqVO
     */
    @GetMapping("/flow-preview")
    @Operation(summary = "流程预览")
    public CommonResult<BpmPreviewRespVO> flowPreview(@Valid BpmPreviewReqVO reqVO) {
        log.info("流程预览: {}", reqVO);
        return CommonResult.success(bpmExecService.flowPreview(reqVO)) ;
    }
}