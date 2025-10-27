package com.cmsr.onebase.module.bpm.runtime.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.bpm.runtime.service.BpmExecService;
import com.cmsr.onebase.module.bpm.runtime.vo.ListActButtonRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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
    public CommonResult<ListActButtonRespVO> listActButtons(@RequestParam(value = "entityDataId", required = false) String entityDataId, @RequestParam("businessId") String businessId) {
        log.info("获取流程实例的操作按钮: {}, {}", entityDataId, businessId);
        ListActButtonRespVO respVO = bpmExecService.getActButtons(entityDataId, businessId);
        return CommonResult.success(respVO);
    }
}