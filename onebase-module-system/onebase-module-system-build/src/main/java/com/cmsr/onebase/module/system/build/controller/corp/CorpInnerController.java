package com.cmsr.onebase.module.system.build.controller.corp;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.dal.dataobject.corp.CorpDO;
import com.cmsr.onebase.module.system.service.corp.CorpService;
import com.cmsr.onebase.module.system.vo.corp.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;

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
public class CorpInnerController {

    @Resource
    private CorpService corpService;


    @GetMapping("/page")
    @Operation(summary = "获得企业列表-分页")
    @PreAuthorize("@ss.hasPermission('corp:app-auth:query')")
    public CommonResult<PageResult<CorpRespVO>> getCorpPage(@Valid CorpPageReqVO pageReqVO) {
        PageResult<CorpRespVO> pageResult = corpService.getCorpAppsPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping(value = {"/simple-list"})
    @Operation(summary = "获取企业精简信息列表-不分页", description = "只包含被开启的企业，主要用于前端的下拉选项")
    @PreAuthorize("@ss.hasPermission('corp:app-auth:query')")
    public CommonResult<List<CorpSimpleRespVO>> getSimpleCorpList() {
        List<CorpDO> list = corpService.getSimpleCorpList(CommonStatusEnum.ENABLE.getStatus());
        return success(BeanUtils.toBean(list, CorpSimpleRespVO.class));
    }

    @PostMapping("/update-status")
    @Operation(summary = "企业启用/禁用")
    @PreAuthorize("@ss.hasPermission('corp:info:update')")
    public CommonResult<Boolean> updateStatus(@RequestParam("id") Long id, @RequestParam("status") Long status) {
        corpService.updateStatus(id, status);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得企业详情")
    @PreAuthorize("@ss.hasPermission('corp:info:query')")
    public CommonResult<CorpRespVO> getCorp(@RequestParam("id") Long id) {
        CorpRespVO corp = corpService.getCorp(id);
        return success(corp);
    }

}
