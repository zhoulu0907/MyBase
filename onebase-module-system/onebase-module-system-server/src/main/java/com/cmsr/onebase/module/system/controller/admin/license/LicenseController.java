package com.cmsr.onebase.module.system.controller.admin.license;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.controller.admin.license.vo.LicensePageReqVO;
import com.cmsr.onebase.module.system.controller.admin.license.vo.LicensePageRespVO;
import com.cmsr.onebase.module.system.controller.admin.license.vo.LicenseRespVO;
import com.cmsr.onebase.module.system.controller.admin.license.vo.LicenseSaveReqVO;
import com.cmsr.onebase.module.system.convert.license.LicenseConvert;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.enums.license.LicenseStatusEnum;
import com.cmsr.onebase.module.system.service.license.LicenseService;
import com.cmsr.onebase.module.system.util.encrypt.SM4LicenseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Resource
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
    @PreAuthorize("@ss.hasPermission('system:platform-admin:query')")
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
    @GetMapping("/simple-list")
    @PreAuthorize("ss.hasAuthority('system:license:query')")
    @Operation(summary = "获取全部License（精简信息）")
    public List<LicensePageRespVO> getSimpleLicenseList() {
        List<LicenseDO> list = licenseService.getSimpleLicenseList();
        return LicenseConvert.INSTANCE.convertList(list);
    }


    @PostMapping("/upload")
    @Operation(summary = "上传加密License文件并入库")
    @Parameters({
            @Parameter(name = "file", description = "加密的license.lic.sm4文件", required = true),
            @Parameter(name = "updateSupport", description = "是否支持更新，默认为 false", example = "true")
    })
    @PreAuthorize("@ss.hasPermission('system:platform-admin:query')")
    public CommonResult<Long> importLicense(@RequestParam("file") MultipartFile file,
                                            @RequestParam(value = "updateSupport", required = false, defaultValue = "false") Boolean updateSupport) throws Exception {
        // 创建临时文件用于解密
        File tempEncryptedFile = File.createTempFile("license_encrypted_", ".sm4");
        File tempDecryptedFile = File.createTempFile("license_decrypted_", ".lic");
        
        try {
            // 将上传的文件保存到临时文件
            file.transferTo(tempEncryptedFile);
            
            // 使用SM4解密文件
            String key = "admin123"; // 这里应该从配置或安全地方获取
            SM4LicenseUtil.decryptFile(key, tempEncryptedFile, tempDecryptedFile);
            
            // 读取解密后的文件内容
            String content = org.apache.commons.io.FileUtils.readFileToString(tempDecryptedFile, "UTF-8");
            
            // 解析JSON内容
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            LicenseSaveReqVO licenseSaveReqVO = objectMapper.readValue(content, LicenseSaveReqVO.class);
            
            // 如果不支持更新，设置ID为null以创建新记录
            if (!updateSupport) {
                licenseSaveReqVO.setId(null);
            }
            
            // 如果ID为null则创建，否则更新
            Long licenseId;
            if (licenseSaveReqVO.getId() == null) {
                // 创建License时，将状态设置为ENABLE
                licenseSaveReqVO.setStatus(LicenseStatusEnum.ENABLE.getStatus());
                licenseId = licenseService.createLicense(licenseSaveReqVO);
            } else {
                licenseService.updateLicense(licenseSaveReqVO);
                licenseId = licenseSaveReqVO.getId();
            }
            
            // 如果是新创建的license，将其他所有已认证的license更新为已失效状态
            if (licenseSaveReqVO.getId() == null) {
                List<LicenseDO> licenses = licenseService.getSimpleLicenseList();
                for (LicenseDO license : licenses) {
                    // 将除了当前创建的license之外的所有enable状态的license更新为disable状态
                    if (!license.getId().equals(licenseId) && LicenseStatusEnum.ENABLE.getStatus().equals(license.getStatus())) {
                        LicenseSaveReqVO updateReqVO = new LicenseSaveReqVO();
                        updateReqVO.setId(license.getId());
                        updateReqVO.setStatus(LicenseStatusEnum.DISABLE.getStatus());
                        licenseService.updateLicense(updateReqVO);
                    }
                }
            }
            
            return CommonResult.success(licenseId);
        } finally {
            // 清理临时文件
            tempEncryptedFile.delete();
            tempDecryptedFile.delete();
        }
    }

    /**
     * 导出凭证
     */
    @GetMapping("/export")
    @Operation(summary = "导出凭证")
    @PreAuthorize("@ss.hasPermission('system:platform-admin:query')")
    public void exportLicense(@RequestParam("id") Long id, HttpServletResponse response) throws Exception {
        // 从数据库中根据ID查询license
        LicenseDO license = licenseService.getLicense(id);

        if (license == null) {
            throw new RuntimeException("未找到ID为 " + id + " 的凭证");
        }

        // 构造要写入的JSON对象，仿照license.lic文件格式
        Map<String, Object> licenseMap = new HashMap<>();
        licenseMap.put("enterpriseName", license.getEnterpriseName());
        licenseMap.put("enterpriseCode", license.getEnterpriseCode());
        licenseMap.put("enterpriseAddress", license.getEnterpriseAddress());
        licenseMap.put("platformType", license.getPlatformType());
        licenseMap.put("expireTime", license.getExpireTime() != null ? license.getExpireTime().toString() : null);
        licenseMap.put("createTime", license.getCreateTime() != null ? license.getCreateTime().toString() : null);
        licenseMap.put("status", license.getStatus());
        licenseMap.put("isTrial", license.getIsTrial());
        // 添加示例文件中的固定字段
        licenseMap.put("superAdmin", "OneBase01");
        licenseMap.put("authStatus", "已认证");
        licenseMap.put("systemVersion", "v1.0.0");
        licenseMap.put("tenantCount", "2");
        
        // 设置响应头，返回加密文件
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"license.lic.sm4\"");

        // 创建临时文件用于加密
        File tempPlainFile = File.createTempFile("license_plain_", ".lic");
        File tempEncryptedFile = File.createTempFile("license_encrypted_", ".lic.sm4");
        
        try {
            // 将license信息写入临时明文文件
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String jsonContent = objectMapper.writeValueAsString(licenseMap);
            org.apache.commons.io.FileUtils.writeStringToFile(tempPlainFile, jsonContent, "UTF-8");
            
            // 使用SM4加密文件，密钥为admin123
            String key = "admin123";
            SM4LicenseUtil.encryptFile(key, tempPlainFile, tempEncryptedFile);
            
            // 将加密后的文件内容写入响应输出流
            byte[] encryptedContent = org.apache.commons.io.FileUtils.readFileToByteArray(tempEncryptedFile);
            response.getOutputStream().write(encryptedContent);
            response.getOutputStream().flush();
        } finally {
            // 清理临时文件
            tempPlainFile.delete();
            tempEncryptedFile.delete();
        }
    }
}
