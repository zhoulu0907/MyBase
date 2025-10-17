package com.cmsr.onebase.module.bpm.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
//import com.cmsr.onebase.module.bpm.build.service.FlowInfoBuildService;
//import com.cmsr.onebase.module.bpm.build.vo.FlowDefinitionVO;
//import com.cmsr.onebase.module.bpm.build.vo.FlowInfoReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.dromara.warm.flow.core.dto.DefJson;
import org.dromara.warm.flow.core.dto.FlowCombine;
import org.dromara.warm.flow.core.service.DefService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "审批流")
@RestController
@RequestMapping("/bpm/flow")
@Validated
@Slf4j
public class BpmFlowController {
     @Resource
     private DefService defService;
    @PostMapping("/save")
    @Operation(summary = "保存流程")
    //@PreAuthorize("@ss.hasPermission('bpm:engine:execute')")
    public CommonResult<Boolean> saveFlow(@Valid @RequestBody DefJson defJson, @RequestHeader("onlyNodeSkip") boolean onlyNodeSkip) throws Exception {
        log.info("流程请求信息: {}", defJson);
        defService.saveDef(defJson,onlyNodeSkip);
        return CommonResult.success(true);
    }
    @PostMapping("/query")
    @Operation(summary = "查询流程")
    //@PreAuthorize("@ss.hasPermission('bpm:engine:execute')")
    public CommonResult<DefJson> query(@RequestParam @NotNull(message = "流程ID不能为空") Long flowId) {
        log.info("查询流程: {}", flowId);
        DefJson defJson = defService.queryDesign(flowId);
        return CommonResult.success(defJson);
    }
    @PostMapping("/delete")
    @Operation(summary = "删除流程")
    //@PreAuthorize("@ss.hasPermission('bpm:engine:execute')")
    public CommonResult<Boolean> delete(@RequestParam @NotNull(message = "流程ID不能为空") Long flowId) {
        log.info("删除流程: {}", flowId);
        boolean result = defService.removeById(flowId);
        return CommonResult.success(result);
    }

}