package com.cmsr.onebase.module.bpm.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.bpm.build.service.BpmVersionMgmtService;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmDeleteReqVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author liyang
 * @date 2025-10-21
 */
@Tag(name = "流程版本管理")
@RestController
@RequestMapping("/bpm/version-mgmt")
@Validated
@Slf4j
public class BpmVersionMgmtController {

    @Resource
    private BpmVersionMgmtService bpmVersionMgmtService;

    @PostMapping("/delete")
    @Operation(summary = "删除流程")
    public CommonResult<Boolean> delete(@Valid @RequestBody BpmDeleteReqVo reqVo) {
        log.info("删除流程: {}", reqVo);
        bpmVersionMgmtService.delete(reqVo);
        return CommonResult.success(true);
    }
}