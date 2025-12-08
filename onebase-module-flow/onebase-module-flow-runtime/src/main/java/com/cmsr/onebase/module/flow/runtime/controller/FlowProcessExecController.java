package com.cmsr.onebase.module.flow.runtime.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.flow.core.handler.FlowCacheHandler;
import com.cmsr.onebase.module.flow.runtime.service.FlowProcessExecService;
import com.cmsr.onebase.module.flow.runtime.vo.FormTriggerReqVO;
import com.cmsr.onebase.module.flow.runtime.vo.FormTriggerRespVO;
import com.cmsr.onebase.module.flow.runtime.vo.QueryFormTriggerRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程执行 - 执行控制器
 */
@Setter
@RestController
@RequestMapping("/flow/exec/")
@Tag(name = "流程执行", description = "流程执行相关接口")
@Validated
public class FlowProcessExecController {

    @Autowired
    private FlowProcessExecService flowProcessExecService;

    @Autowired
    private FlowCacheHandler flowCacheHandler;

    @GetMapping("/form/query")
    @Operation(summary = "查询页面触发列表")
    public CommonResult<List<QueryFormTriggerRespVO>> queryFormTrigger(
            @RequestParam("pageUuid") String pageUuid) {
        List<QueryFormTriggerRespVO> result = flowProcessExecService.queryFormTrigger(pageUuid);
        return CommonResult.success(result);
    }

    @PostMapping("/form/trigger")
    @Operation(summary = "触发页面")
    public CommonResult<FormTriggerRespVO> triggerForm(@RequestBody @Validated FormTriggerReqVO reqVO) {
        FormTriggerRespVO result = flowProcessExecService.triggerForm(reqVO);
        return CommonResult.success(result);
    }

    @PostMapping("/flow-handler/update")
    @Operation(summary = "更新流程")
    public CommonResult<String> updateProcess(@RequestParam("applicationId") Long applicationId) {
        String result = flowCacheHandler.onApplicationChange(applicationId);
        return CommonResult.success(result);
    }

    @PostMapping("/flow-handler/delete")
    @Operation(summary = "删除流程")
    public CommonResult<String> deleteProcess(@RequestParam("applicationId") Long applicationId) {
        String result = flowCacheHandler.onApplicationDelete(applicationId);
        return CommonResult.success(result);
    }


}
