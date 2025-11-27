package com.cmsr.onebase.module.system.build.controller.corp;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.dal.dataobject.corp.CorpDO;
import com.cmsr.onebase.module.system.service.corp.CorpService;
import com.cmsr.onebase.module.system.vo.corp.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "平台服务-企业")
@RestController
@RequestMapping("/system/corp")
@Validated
public class CorpController {

    @Resource
    private CorpService corpService;

    @PostMapping("/create")
    @Operation(summary = "创建企业")
    @PreAuthorize("@ss.hasPermission('tenant:corp:create')")
    public CommonResult<CorpAdminUserRespVO> createCorpCombined(@RequestBody @Valid CorpCombinedVo reqVO) {
        return success(corpService.createCorpCombined(reqVO));
    }


    @PostMapping("/update")
    @Operation(summary = "更新企业")
    @PreAuthorize("@ss.hasPermission('tenant:corp:update')")
    public CommonResult<Boolean> updateCorp(@RequestBody @Valid CorpUpdateReqVO reqVO) {
        corpService.updateCorp(reqVO);
        return success(true);
    }


    @PostMapping("/update-status")
    @Operation(summary = "企业启用/禁用")
    @PreAuthorize("@ss.hasPermission('tenant:corp:enable')")
    public CommonResult<Boolean> updateStatus(@RequestParam("id") Long id, @RequestParam("status") Long status) {
        corpService.updateStatus(id, status);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除企业")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('tenant:corp:delete')")
    public CommonResult<Boolean> deleteCorp(@RequestParam("id") Long id) {
        corpService.deleteCorp(id);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得企业列表-分页")
    @PreAuthorize("@ss.hasPermission('tenant:corp:query')")
    public CommonResult<PageResult<CorpRespVO>> getCorpPage(@Valid CorpPageReqVO pageReqVO) {
        PageResult<CorpRespVO> pageResult = corpService.getCorpAppsPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping(value = {"/simple-list"})
    @Operation(summary = "获取企业精简信息列表-不分页", description = "只包含被开启的企业，主要用于前端的下拉选项")
    @PreAuthorize("@ss.hasPermission('tenant:corp:query')")
    public CommonResult<List<CorpSimpleRespVO>> getSimpleCorpList() {
        List<CorpDO> list = corpService.getSimpleCorpList(CommonStatusEnum.ENABLE.getStatus());
        return success(BeanUtils.toBean(list, CorpSimpleRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得企业详情")
    @PreAuthorize("@ss.hasPermission('tenant:corp:query')")
    public CommonResult<CorpRespVO> getCorp(@RequestParam("id") Long id) {
        CorpRespVO corp = corpService.getCorp(id);
        return success(corp);
    }

}
