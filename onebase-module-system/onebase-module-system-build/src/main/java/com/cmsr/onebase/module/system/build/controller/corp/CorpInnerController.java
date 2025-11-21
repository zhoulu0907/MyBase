package com.cmsr.onebase.module.system.build.controller.corp;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.system.service.corp.CorpService;
import com.cmsr.onebase.module.system.vo.corp.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 企业 Controller
 *
 * @author ggq
 * @date 2025-10-20
 */
@Tag(name = "平台服务-企业")
@RestController
@RequestMapping("/corp")
@Validated
public class CorpInnerController {

    @Resource
    private CorpService corpService;


    @PostMapping("/update-status")
    @Operation(summary = "企业启用/禁用")
    @PreAuthorize("@ss.hasPermission('corp:app-auth:enable')")
    public CommonResult<Boolean> updateStatus(@RequestParam("id") Long id, @RequestParam("status") Long status) {
        corpService.updateStatus(id, status);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得企业详情")
    @PreAuthorize("@ss.hasPermission('corp:info')")
    public CommonResult<CorpRespVO> getCorp(@RequestParam("id") Long id) {
        CorpRespVO corp = corpService.getCorp(id);
        return success(corp);
    }

}
