package com.cmsr.onebase.module.bpm.runtime.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.core.vo.BpmFormDataPageReqVO;
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
import java.util.Map;


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
    private BpmInstanceService bpmInstanceService;

    @PostMapping("/submit")
    @Operation(summary = "流程发起")
    public CommonResult<BpmSubmitRespVO> exec(@RequestBody @Valid BpmSubmitReqVO reqVO) {
        log.info("执行流程实例的操作按钮: {}", reqVO);
        BpmSubmitRespVO respVO = bpmInstanceService.submit(reqVO);
        return CommonResult.success(respVO);
    }

    @PostMapping("/exec-task")
    public CommonResult<Boolean> exec(@RequestBody @Valid ExecTaskReqVO reqVO) {
        log.info("执行流程实例的操作按钮: {}", reqVO);
        bpmInstanceService.execTask(reqVO);
        return CommonResult.success(true);
    }

    @GetMapping("/get-operator-record")
    @Operation(summary = "获取流程实例的操作记录")
    public CommonResult<List<BpmOperatorRecordRespVO.OperatorRecord>> getOperatorRecord(@RequestParam("instanceId") Long instanceId) {
        log.info("获取流程实例的操作记录: {}", instanceId);
        List<BpmOperatorRecordRespVO.OperatorRecord> records = bpmInstanceService.getOperatorRecord(instanceId);
        return CommonResult.success(records);
    }

    @GetMapping("/get-form-detail")
    public CommonResult<BpmTaskDetailRespVO> getFormDetail(@Valid BpmTaskDetailReqVO reqVO) {
        log.info("获取流程详情: {}", reqVO);
        return CommonResult.success(bpmInstanceService.getFormDetail(reqVO));
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
        return CommonResult.success(bpmInstanceService.flowPredict(reqVO)) ;
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
        return CommonResult.success(bpmInstanceService.flowPreview(reqVO)) ;
    }

    /**
     * 获取列表数据
     *
     * @param reqVO
     */
    @PostMapping("/form-data-page")
    @Operation(summary = "流程实体数据分页")
    public CommonResult<PageResult<Map<String, Object>>> formDataPage(@RequestBody BpmFormDataPageReqVO   reqVO) {
        log.info("获取列表数据: {}", reqVO);
        return CommonResult.success(bpmInstanceService.formDataPage(reqVO)) ;
    }

}