package com.cmsr.onebase.module.bpm.runtime.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.runtime.service.BpmDelegationService;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmDelegationPageReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmDelegationPageResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 流程代理
 *
 * @author liyang
 * @date 2025-11-10
 */
@Tag(name = "流程代理管理")
@RestController
@RequestMapping("/bpm/delegation")
@Validated
@Slf4j
public class BpmDelegationController {
    @Resource
    private BpmDelegationService bpmDelegationService;

    @PostMapping("/create")
    @Operation(summary = "创建流程代理")
    public void create() {
        // todo: 补充业务逻辑，被代理人取当前登录用户
    }

    @PostMapping("/update")
    @Operation(summary = "更新流程代理")
    public void update() {
        // todo: 补充业务逻辑 只有当前被代理人可以修改
    }

    @PostMapping("/revoke")
    @Operation(summary = "撤销流程代理")
    public void revoke() {
        // todo: 补充业务逻辑
    }

    @GetMapping("/page")
    @Operation(summary = "流程代理列表查询")
    public CommonResult<PageResult<BpmDelegationPageResVO>> page(@Valid BpmDelegationPageReqVO reqVo) {
        log.info("流程代理列表查询: {}", reqVo);
        PageResult<BpmDelegationPageResVO> pageResult = bpmDelegationService.getDelegationPage(reqVo); // 修正为合理方法名
        return CommonResult.success(pageResult);
    }

}