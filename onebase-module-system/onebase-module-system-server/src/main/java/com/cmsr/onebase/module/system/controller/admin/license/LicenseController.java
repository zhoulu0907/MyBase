package com.cmsr.onebase.module.system.controller.admin.license;

import cn.hutool.extra.servlet.JakartaServletUtil;
import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.security.core.LoginUser;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.module.system.controller.admin.license.vo.*;
import com.cmsr.onebase.module.system.convert.license.LicenseConvert;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.enums.license.LicenseSecretKeyEnum;
import com.cmsr.onebase.module.system.enums.license.LicenseStatusEnum;
import com.cmsr.onebase.module.system.service.license.LicenseService;
import com.cmsr.onebase.module.system.util.encrypt.SM4LicenseUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;
import static com.cmsr.onebase.module.system.util.encrypt.SM4Utils.decryptSm4FileToFile;
import static com.cmsr.onebase.module.system.util.encrypt.SM4Utils.sm4Encrypt;

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


    @PostMapping("/import")
    @Operation(summary = "导入凭证")
    @Parameter(name = "file", description = "加密的license.lic.sm4文件", required = true)
    @PreAuthorize("@ss.hasPermission('system:platform-admin:query')")
    public CommonResult<Long> importLicense(@RequestParam("file") MultipartFile file) throws Exception {
        // 创建备份文件用于解密
        // license/license_encrypted__username_202501010824.sm4/lic
        // 获取当前登录用户名
        String username = SecurityFrameworkUtils.getLoginUserNickname();
        // 获取当前时间，格式yyyyMMddHHmmss
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        // 项目根目录下license目录
        String licenseDirPath = System.getProperty("user.dir") + File.separator + "license";
        File licenseDir = new File(licenseDirPath);
        if (!licenseDir.exists()) {
            licenseDir.mkdirs();
        }
        // 构造文件名
        String baseName = "license_encrypted__" + username + "_" + now;
        String sm4FilePath = licenseDirPath + File.separator + baseName + ".sm4";
        String licFilePath = licenseDirPath + File.separator + baseName + ".lic";

        File sm4File = new File(sm4FilePath);
        File licFile = new File(licFilePath);

        // 保存上传的文件为加密文件
        file.transferTo(sm4File);

        // 解密生成.lic明文文件
        // try {
        //     SM4LicenseUtil.decryptSm4FileToFile(sm4FilePath, licFilePath);
        // } catch (Exception e) {
        //     log.error("解密License文件失败", e);
        //     throw exception(LICENSE_IMPORT_ERROR, e.getMessage());
        // }
        // 解密文件并保存到lic文件
        boolean decryptSuccess = decryptSm4FileToFile(sm4FilePath, LicenseSecretKeyEnum.LICENSE_SECRET_KEY.getSecretKey(), licFilePath);
        String decrypted = null;
        if (decryptSuccess) {
            System.out.println("解密文件已生成: " + licFilePath);

            // 验证解密内容
            decrypted = new String(Files.readAllBytes(Paths.get(licFilePath)), StandardCharsets.UTF_8);
            System.out.println("解密内容: " + decrypted);
        } else {
            System.err.println("解密文件失败！");
        }
        // 读取解密后的字符串
        // String content = FileUtils.readFileToString(licFile, StandardCharsets.UTF_8);
        log.info("解密后的内容: {}", decrypted);

        try {
            // 解析JSON内容
            ObjectMapper objectMapper = new ObjectMapper();
            // 注册JavaTimeModule以支持LocalDateTime反序列化
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            objectMapper.registerModule(javaTimeModule);
            // 添加自定义的LocalDateTime反序列化器
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                    return LocalDateTime.parse(p.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
            });
            objectMapper.registerModule(simpleModule);

            LicenseSaveReqVO licenseSaveReqVO = objectMapper.readValue(decrypted, LicenseSaveReqVO.class);
            licenseSaveReqVO.setLicenseFile("测试用例");
            // 创建License时，将状态设置为ENABLE
            licenseSaveReqVO.setStatus(LicenseStatusEnum.ENABLE.getStatus());
            Long licenseId = licenseService.createLicense(licenseSaveReqVO);

            // 如果是新创建的license，将其他所有已认证的license更新为已失效状态
            List<LicenseDO> licenses = licenseService.getEnableLicenseList();
            for (LicenseDO license : licenses) {
                // 将除了当前创建的license之外的所有enable状态的license更新为disable状态
                if (!license.getId().equals(licenseId)) {
                    LicenseSaveReqVO updateReqVO = new LicenseSaveReqVO();
                    updateReqVO.setId(license.getId());
                    updateReqVO.setStatus(LicenseStatusEnum.DISABLE.getStatus());
                    licenseService.updateLicense(updateReqVO);
                }
            }
            return CommonResult.success(licenseId);
        } catch (Exception e) {
            log.error("解析License文件内容失败", e);
            throw exception(LICENSE_IMPORT_ERROR, e.getMessage());
        }
    }

    /**
     * 导出凭证
     */
    @GetMapping("/export")
    @Operation(summary = "导出加密凭证")
    @PreAuthorize("@ss.hasPermission('system:platform-admin:query')")
    public void exportLicense(@RequestParam("id") Long id, HttpServletResponse response) throws Exception {
        // 从数据库中根据ID查询license
        LicenseDO license = licenseService.getLicense(id);

        if (license == null) {
            throw exception(LICENSE_NOT_EXISTS, id);
        }
        LicenseExportRespVO licenseExportRespVO = new LicenseExportRespVO();

        licenseExportRespVO.setEnterpriseName(license.getEnterpriseName());
        licenseExportRespVO.setEnterpriseCode(license.getEnterpriseCode());
        licenseExportRespVO.setEnterpriseAddress(license.getEnterpriseAddress());
        licenseExportRespVO.setPlatformType(license.getPlatformType());
        licenseExportRespVO.setExpireTime(license.getExpireTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        licenseExportRespVO.setStatus(license.getStatus());
        licenseExportRespVO.setTenantLimit(license.getTenantLimit().toString());
        licenseExportRespVO.setUserLimit(license.getUserLimit().toString());
        // 设置响应头，返回加密文件
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"license.lic.sm4\"");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Expires", "0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        // 将license信息写入json字符串
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonContent = objectMapper.writeValueAsString(licenseExportRespVO);
        // 使用SM4加密字符串,将加密后的内容写入响应输出流
        // byte[] encryptedContent = SM4LicenseUtil.encryptStringToSm4Bytes(jsonContent);
        String sm4Encrypt = sm4Encrypt(jsonContent, LicenseSecretKeyEnum.LICENSE_SECRET_KEY.getSecretKey());
        System.out.println("加密后的长度: " + sm4Encrypt.length());
        // byte[] jsonb = jsonContent.getBytes("utf-8");
        // response.setContentLength(encryptedContent.length);
        response.getOutputStream().write(sm4Encrypt.getBytes());
        response.getOutputStream().flush();

        // JakartaServletUtil.write(response, encryptedContent, MediaType.APPLICATION_JSON_UTF8_VALUE);

    }
}
