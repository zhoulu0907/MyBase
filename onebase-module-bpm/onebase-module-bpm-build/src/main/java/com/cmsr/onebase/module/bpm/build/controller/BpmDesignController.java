package com.cmsr.onebase.module.bpm.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.bpm.build.service.BpmDesignService;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDeleteReqVo;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


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

    @PostMapping("/delete")
    @Operation(summary = "删除流程")
    public CommonResult<Boolean> delete(@Valid @RequestBody BpmDeleteReqVo reqVo) {
        log.info("删除流程: {}", reqVo);
        bpmDesignService.delete(reqVo);
        return CommonResult.success(true);
    }
}