package com.cmsr.onebase.module.bpm.runtime.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.runtime.service.BpmAgentService;
import com.cmsr.onebase.module.bpm.runtime.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 流程代理
 *
 * @author liyang
 * @date 2025-11-10
 */
@Tag(name = "流程代理管理")
@RestController
@RequestMapping("/bpm/agent")
@Validated
@Slf4j
public class BpmAgentController {
    @Resource
    private BpmAgentService bpmAgentService;

    @PostMapping("/create")
    @Operation(summary = "创建流程代理")
    public CommonResult<Boolean> create(@RequestBody @Validated BpmAgentInsertReqVO reqVO) {
        log.info("创建流程代理: {}", reqVO);
        bpmAgentService.create(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/update")
    @Operation(summary = "更新流程代理")
    public CommonResult<Boolean>  update(@RequestBody @Validated BpmAgentUpdateReqVO reqVO) {
        log.info("更新流程代理: {}", reqVO);
        bpmAgentService.update(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/revoke")
    @Operation(summary = "撤销流程代理")
    public CommonResult<Boolean>  revoke(@RequestBody @Validated BpmAgentRevokeReqVO reqVO) {
        log.info("撤销流程代理: {}", reqVO);
        bpmAgentService.revoke(reqVO);
        return CommonResult.success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "流程代理列表查询")
    public CommonResult<PageResult<BpmAgentPageResVO>> page(@Valid BpmAgentPageReqVO reqVo) {
        log.info("流程代理列表查询: {}", reqVo);
        PageResult<BpmAgentPageResVO> pageResult = bpmAgentService.getAgentPage(reqVo); // 修正为合理方法名
        return CommonResult.success(pageResult);
    }

}