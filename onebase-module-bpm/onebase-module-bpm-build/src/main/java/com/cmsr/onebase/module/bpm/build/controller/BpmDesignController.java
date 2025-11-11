package com.cmsr.onebase.module.bpm.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.bpm.build.service.BpmDesignService;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignVO;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmPublishReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * @author liyang
 * @date 2025-10-21
 */
@Tag(name = "审批流设计")
@RestController
@RequestMapping("/bpm/design")
@Validated
@Slf4j
public class BpmDesignController {

     @Resource
     private BpmDesignService bpmDesignService;

    @PostMapping("/save")
    @Operation(summary = "保存流程")
    public CommonResult<Long> saveFlow(@Valid @RequestBody BpmDesignVO flowDesignVO) {
        log.info("流程请求信息: {}", flowDesignVO);
        Long flowId = bpmDesignService.save(flowDesignVO);
        return CommonResult.success(flowId);
    }

    @GetMapping("/get")
    @Operation(summary = "获取流程")
    public CommonResult<BpmDesignVO> query(@RequestParam("id") Long flowId) {
        log.info("查询流程: {}", flowId);
        BpmDesignVO flowDesignVO = bpmDesignService.queryById(flowId);
        return CommonResult.success(flowDesignVO);
    }

    @GetMapping("/get-by-business-id")
    @Operation(summary = "根据业务ID查询默认流程")
    public CommonResult<BpmDesignVO> queryByBusinessId(@RequestParam("businessId") Long businessId) {
        log.info("查询流程: {}", businessId);
        BpmDesignVO flowDesignVO = bpmDesignService.queryByBusinessId(businessId);
        return CommonResult.success(flowDesignVO);
    }

    @PostMapping("/publish")
    @Operation(summary = "发布流程")
    public CommonResult<Boolean> publish(@Valid @RequestBody BpmPublishReqVO reqVo) {
        log.info("发布流程: {}", reqVo);
        bpmDesignService.publish(reqVo);
        return CommonResult.success(true);
    }
}