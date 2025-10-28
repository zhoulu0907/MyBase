package com.cmsr.onebase.module.bpm.runtime.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.bpm.runtime.service.BpmExecService;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmStartReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.ExecActButtonReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.ListActButtonRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 流程执行控制器
 *
 * @author liyang
 * @date 2025-10-21
 */
@Tag(name = "流程执行管理")
@RestController
@RequestMapping("/bpm/exec")
@Validated
@Slf4j
public class BpmExecController {

    @Resource
    private BpmExecService bpmExecService;

    @GetMapping("/list-act-buttons")
    @Operation(summary = "获取流程实例的操作按钮")
    public CommonResult<ListActButtonRespVO> listActButtons(@RequestParam(value = "taskId", required = false) String taskId, @RequestParam("businessId") String businessId) {
        log.info("获取流程实例的操作按钮: {}, {}", taskId, businessId);
        ListActButtonRespVO respVO = bpmExecService.getActButtons(taskId, businessId);
        return CommonResult.success(respVO);
    }

    @PostMapping("/start")
    @Operation(summary = "流程发起")
    public CommonResult<String> exec(@RequestBody @Validated BpmStartReqVO reqVO) {
        log.info("执行流程实例的操作按钮: {}", reqVO);
        String entityDataId = bpmExecService.start(reqVO);
        return CommonResult.success(entityDataId);
    }

    @PostMapping("/perform-act")
    public CommonResult<String> exec(@RequestBody @Validated ExecActButtonReqVO reqVO) {
        log.info("执行流程实例的操作按钮: {}", reqVO);
        String entityDataId = bpmExecService.execActButton(reqVO);
        return CommonResult.success(entityDataId);
    }
}