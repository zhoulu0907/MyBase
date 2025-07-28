package com.cmsr.onebase.module.system.controller.admin.license;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.convert.license.LicenseConvert;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.service.license.LicenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import java.util.List;

/**
 * License 管理接口
 *
 * 提供License的增删改查等接口。
 *
 * @author matianyu
 * @date 2025-07-25
 */
@RestController
@RequestMapping("/system/license")
@Tag(name = "License管理")
public class LicenseController {

    @Autowired
    private LicenseService licenseService;

    /**
     * 创建License
     *
     * @param reqVO License创建请求参数
     * @return License主键ID
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('system:license:create')")
    @Operation(summary = "创建License")
    public Long createLicense(@RequestBody LicenseSaveReqVO reqVO) {
        return licenseService.createLicense(reqVO);
    }

    /**
     * 更新License
     *
     * @param reqVO License更新请求参数
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('system:license:update')")
    @Operation(summary = "更新License")
    public void updateLicense(@RequestBody LicenseSaveReqVO reqVO) {
        licenseService.updateLicense(reqVO);
    }

    /**
     * 删除License
     *
     * @param id License主键ID
     */
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('system:license:delete')")
    @Operation(summary = "删除License")
    public void deleteLicense(@RequestParam("id") Long id) {
        licenseService.deleteLicense(id);
    }

    /**
     * 获取License详情
     *
     * @param id License主键ID
     * @return License详情
     */
    @GetMapping("/get")
    @PreAuthorize("hasAuthority('system:license:query')")
    @Operation(summary = "获取License详情")
    public LicenseRespVO getLicense(@RequestParam("id") Long id) {
        LicenseDO license = licenseService.getLicense(id);
        return LicenseConvert.INSTANCE.convert(license);
    }

    /**
     * 分页查询License
     *
     * @param reqVO 分页查询参数
     * @return 分页结果
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:license:query')")
    @Operation(summary = "分页查询License")
    public CommonResult<PageResult<LicensePageRespVO>> getLicensePage(@Valid LicensePageReqVO reqVO) {
        PageResult<LicenseDO> pageResult = licenseService.getLicensePage(reqVO);
        return CommonResult.success(BeanUtils.toBean(pageResult, LicensePageRespVO.class));
    }

    /**
     * 获取全部License（精简信息）
     *
     * @return License列表
     */
    @GetMapping("/list-all-simple")
    @PreAuthorize("hasAuthority('system:license:query')")
    @Operation(summary = "获取全部License（精简信息）")
    public List<LicensePageRespVO> getSimpleLicenseList() {
        List<LicenseDO> list = licenseService.getSimpleLicenseList();
        return LicenseConvert.INSTANCE.convertList(list);
    }
}
