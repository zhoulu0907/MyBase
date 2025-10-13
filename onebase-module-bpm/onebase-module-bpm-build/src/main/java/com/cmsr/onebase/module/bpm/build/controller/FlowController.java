package com.cmsr.onebase.module.bpm.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.bpm.build.service.FlowInfoBuildService;
import com.cmsr.onebase.module.bpm.build.vo.FlowDefinitionVO;
import com.cmsr.onebase.module.bpm.build.vo.FlowInfoReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "审批流")
@RestController
@RequestMapping("/bpm/flow")
@Validated
@Slf4j
public class FlowController {
     @Resource
     private FlowInfoBuildService flowInfoBuildService;

    @PostMapping("/save")
    @Operation(summary = "保存流程")
    //@PreAuthorize("@ss.hasPermission('bpm:engine:execute')")
    public CommonResult<Boolean> saveFlow(@Valid @RequestBody FlowInfoReqVO reqVO) {
        log.info("流程请求信息: {}", reqVO);
        return CommonResult.success(flowInfoBuildService.saveFlowInfo(reqVO));
    }
    @PostMapping("/query")
    @Operation(summary = "查询流程")
    //@PreAuthorize("@ss.hasPermission('bpm:engine:execute')")
    public CommonResult<FlowInfoReqVO> query(@RequestParam @NotNull(message = "流程ID不能为空") Long flowId) {
        log.info("查询流程: {}", flowId);
        FlowInfoReqVO flowInfoReqVO = flowInfoBuildService.queryByFlowId(flowId);
        return CommonResult.success(flowInfoReqVO);
    }
    @PostMapping("/queryByFormId")
    @Operation(summary = "根据表单ID查询流程列表")
    //@PreAuthorize("@ss.hasPermission('bpm:engine:execute')")
    public CommonResult<List<FlowDefinitionVO>> queryByFormId(@RequestParam @NotNull(message = "formID不能为空") Long formId) {
        log.info("根据表单ID查询流程列表表单Id: {}", formId);
        List<FlowDefinitionVO> flowDefinitionVOS = flowInfoBuildService.queryByFormId(formId);
        return CommonResult.success(flowDefinitionVOS);
    }
}