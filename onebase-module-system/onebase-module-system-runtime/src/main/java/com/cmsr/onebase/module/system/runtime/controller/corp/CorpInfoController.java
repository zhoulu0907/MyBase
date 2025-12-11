package com.cmsr.onebase.module.system.runtime.controller.corp;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.system.service.corp.CorpService;
import com.cmsr.onebase.module.system.vo.corp.CorpRespVO;
import com.cmsr.onebase.module.system.vo.corp.CorpUpdateReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
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
@Tag(name = "企业服务 - 企业管理")
@RestController
@RequestMapping("/corp")
@Validated
public class CorpInfoController {

    @Resource
    private CorpService corpService;

    @GetMapping("/get")
    @Operation(summary = "获得企业详情")
    @PreAuthorize("@ss.hasPermission('corp:info:query')")
    public CommonResult<CorpRespVO> getCorp(@RequestParam("id") Long id) {
        CorpRespVO corp = corpService.getCorp(id);
        return success(corp);
    }

    @PostMapping("/update")
    @Operation(summary = "更新企业")
    @PreAuthorize("@ss.hasPermission('corp:info:update')")
    public CommonResult<Boolean> updateCorp(@RequestBody @Valid CorpUpdateReqVO reqVO) {
        corpService.updateCorp(reqVO);
        return success(true);
    }


}
