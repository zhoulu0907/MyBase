package com.cmsr.onebase.module.system.controller.admin.license;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.convert.license.LicenseConvert;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.service.license.LicenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

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
    @PreAuthorize("ss.hasAuthority('system:license:create')")
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
    @PreAuthorize("ss.hasAuthority('system:license:update')")
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
    @PreAuthorize("ss.hasAuthority('system:license:delete')")
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
    @PreAuthorize("ss.hasAuthority('system:license:query')")
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
    @PreAuthorize("ss.hasAuthority('system:license:query')")
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
    @PreAuthorize("ss.hasAuthority('system:license:query')")
    @Operation(summary = "获取全部License（精简信息）")
    public List<LicensePageRespVO> getSimpleLicenseList() {
        List<LicenseDO> list = licenseService.getSimpleLicenseList();
        return LicenseConvert.INSTANCE.convertList(list);
    }


    @PostMapping("/upload")
    @Operation(summary = "上传凭证")
    @Parameters({
            @Parameter(name = "file", description = "json 文件", required = true),
            @Parameter(name = "updateSupport", description = "是否支持更新，默认为 false", example = "true")
    })
    @PreAuthorize("@ss.hasPermission('system:platform-admin:query')")
    public CommonResult<Long> importLicense(@RequestParam("file") MultipartFile file,
                                            @RequestParam(value = "updateSupport", required = false, defaultValue = "false") Boolean updateSupport) throws Exception {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }
        // 解析JSON文件为LicenseSaveReqVO对象
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        LicenseSaveReqVO reqVo = objectMapper.readValue(file.getInputStream(), LicenseSaveReqVO.class);

        LicenseDO licenseDO = licenseService.getLicenseByStatus("enable");
        // 创建更新对象，将状态设置为已失效
        LicenseSaveReqVO updateLicense = new LicenseSaveReqVO();
        updateLicense.setId(licenseDO.getId());
        updateLicense.setStatus("disable"); // 假设LicenseStatusEnum.INVALID.getStatus()表示已失效状态

        // 更新license状态
        licenseService.updateLicense(updateLicense);
//        licenseService.ge

        // 调用licenseService方法保存数据
        Long license = licenseService.createLicense(reqVo);

        return success(license);

    }

    /**
     * 导出凭证
     */
    @GetMapping("/export/{id}")
    @Operation(summary = "导出凭证")
    @PreAuthorize("@ss.hasPermission('system:platform-admin:query')")
    public void exportLicense(@PathVariable Long id, HttpServletResponse response) throws IOException {
        // 从数据库中根据ID查询license
        LicenseDO license = licenseService.getLicense(id);

        if (license == null) {
            throw new RuntimeException("未找到ID为 " + id + " 的凭证");
        }

        // 设置响应头
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"license_" + id + ".json\"");

        // 将对象写入响应输出流
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(response.getOutputStream(), license);
    }
}
