package com.cmsr.onebase.module.system.controller.admin.platform;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.system.controller.admin.license.LicenseSaveReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.service.license.LicenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.cmsr.onebase.module.system.controller.admin.platform.vo.PlatformInfoReqVo;
import com.cmsr.onebase.module.system.controller.admin.platform.vo.PlatformInfoRespVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 平台信息管理控制器
 */
@RestController
@RequestMapping("/system/platforminfo")
@Tag(name = "平台信息管理")
public class PlatformInfoController {

    @Resource
    private LicenseService licenseService;

    /**
     * 创建平台信息
     */
    @PostMapping
    public Object createPlatformInfo(@RequestBody PlatformInfoReqVo reqVo) {
        // TODO: 实现创建平台信息逻辑
        return null;
    }

    /**
     * 更新平台信息
     */
    @PutMapping
    public Object updatePlatformInfo(@RequestBody PlatformInfoReqVo reqVo) {
        // TODO: 实现更新平台信息逻辑
        return null;
    }

    /**
     * 删除平台信息
     */
    @DeleteMapping("/{id}")
    public Object deletePlatformInfo(@PathVariable Long id) {
        // TODO: 实现删除平台信息逻辑
        return null;
    }

    /**
     * 根据ID获取平台信息
     */
    @GetMapping("/{id}")
    public Object getPlatformInfoById(@PathVariable Long id) {
        // TODO: 实现根据ID获取平台信息逻辑
        return null;
    }

    /**
     * 获取平台信息列表
     */
    @GetMapping("/list-simple")
    @Operation(summary = "获得平台信息和凭证列表")
    @PreAuthorize("@ss.hasPermission('system:platform-admin:query')")
    public CommonResult<PlatformInfoRespVo> listPlatformInfos() {
        // TODO: 实现获取平台信息列表逻辑
        PlatformInfoRespVo respVo = new PlatformInfoRespVo();
        List<LicenseDO> list = licenseService.getSimpleLicenseList();
        respVo.setLicenseList(list);
        return success(respVo);
    }
    
    /**
     * 创建凭证
     */
    @PostMapping("/license/create")
    @Operation(summary = "创建凭证")
    @PreAuthorize("@ss.hasPermission('system:platform-admin:query')")
    public Object createLicense(@RequestBody LicenseSaveReqVO reqVo) {
        // TODO: 实现创建凭证逻辑
            return licenseService.createLicense(reqVo);
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