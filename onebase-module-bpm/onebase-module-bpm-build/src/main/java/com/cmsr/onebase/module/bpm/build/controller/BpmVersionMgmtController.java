package com.cmsr.onebase.module.bpm.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.bpm.build.service.BpmVersionMgmtService;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDefinitionVO;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmDefVersionMgtVO;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmDeleteReqVo;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmGetReqVo;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmUpdateReqVo;
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

import java.util.List;


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
    @PostMapping("/get")
    @Operation(summary = "获取流程版本管理列表")
    public CommonResult<List<BpmDefVersionMgtVO>> getByBusinessId(@Valid @RequestBody BpmGetReqVo reqVo) {
        log.info("获取流程版本管理列表: {}", reqVo);
        return CommonResult.success(bpmVersionMgmtService.getByBusinessId(reqVo));
    }
    @PostMapping("/update-version-alias")
    @Operation(summary = "修改流程版本备注")
    public CommonResult<Boolean> updateVersionAliasById(@Valid @RequestBody BpmUpdateReqVo reqVo) {
        log.info("修改流程版本备注: {}", reqVo);
        bpmVersionMgmtService.updateVersionAliasById(reqVo);
        return CommonResult.success(true);
    }
}