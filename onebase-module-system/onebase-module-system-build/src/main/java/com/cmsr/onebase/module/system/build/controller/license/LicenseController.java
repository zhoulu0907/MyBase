package com.cmsr.onebase.module.system.build.controller.license;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.convert.license.LicenseConvert;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.service.license.LicenseService;
import com.cmsr.onebase.module.system.vo.license.LicensePageReqVO;
import com.cmsr.onebase.module.system.vo.license.LicensePageRespVO;
import com.cmsr.onebase.module.system.vo.license.LicenseRespVO;
import com.cmsr.onebase.module.system.vo.license.LicenseSaveReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * License 管理接口
 * <p>
 * 提供License的增删改查等接口。
 *
 * @author matianyu
 * @date 2025-07-25
 */
@Slf4j
@RestController
@RequestMapping("/system/license")
@Tag(name = "License管理")
@Component("oldLicenseController")
@Deprecated
public class LicenseController {

    @Resource
    private LicenseService licenseService;

    /**
     * 创建License
     *
     * @param reqVO License创建请求参数
     * @return License主键ID
     */
    @PostMapping("/createLicenseFile")
    @PreAuthorize("@ss.hasPermission('system:license:create')")
    @Operation(summary = "创建License文件")
    public void createLicenseFile(@RequestBody LicenseSaveReqVO reqVO, HttpServletResponse response) {
        licenseService.createLicenseFile(reqVO, response);
    }

    // /**
    //  * 更新License
    //  *
    //  * @param reqVO License更新请求参数
    //  */
    // @PostMapping("/update")
    // @PreAuthorize("@ss.hasPermission('system:license:update')")
    // @Operation(summary = "更新License")
    // public void updateLicense(@RequestBody LicenseSaveReqVO reqVO) {
    //     licenseService.updateLicense(reqVO);
    // }

    // /**
    //  * 删除License
    //  *
    //  * @param id License主键ID
    //  */
    // @PostMapping("/delete")
    // @PreAuthorize("@ss.hasPermission('system:license:delete')")
    // @Operation(summary = "删除License")
    // public void deleteLicense(@RequestParam("id") Long id) {
    //     licenseService.deleteLicense(id);
    // }

    /**
     * 获取License详情
     *
     * @param id License主键ID
     * @return License详情
     */
    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermission('system:license:query')")
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
    @PreAuthorize("@ss.hasPermission('system:license:query')")
    @Operation(summary = "分页查询License")
    public CommonResult<PageResult<LicensePageRespVO>> getLicensePage(@Valid LicensePageReqVO reqVO) {
        PageResult<LicenseDO> pageResult = licenseService.getLicensePage(reqVO);
        CommonResult<PageResult<LicensePageRespVO>> success = CommonResult.success(BeanUtils.toBean(pageResult, LicensePageRespVO.class));
        return success;
    }

    /**
     * 获取全部License（精简信息）
     *
     * @return License列表
     */
    @GetMapping("/simple-list")
    @PreAuthorize("@ss.hasPermission('system:license:query')")
    @Operation(summary = "获取全部License（精简信息）")
    public List<LicensePageRespVO> getSimpleLicenseList() {
        List<LicenseDO> list = licenseService.getSimpleLicenseList();
        return LicenseConvert.INSTANCE.convertList(list);
    }


    @PostMapping("/import")
    @Operation(summary = "导入凭证")
    @Parameter(name = "file", description = "加密的license.lic.sm4文件", required = true)
    @PreAuthorize("@ss.hasPermission('system:license:query')")
    public CommonResult<Long> importLicense(@RequestParam("file") MultipartFile file) {
        Long licenseId = licenseService.importLicense(file);
        return CommonResult.success(licenseId);
    }

}
