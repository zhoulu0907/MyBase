package com.cmsr.onebase.module.system.controller.admin.platform;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.system.controller.admin.oauth2.vo.user.OAuth2UserInfoRespVO;
import com.cmsr.onebase.module.system.controller.admin.platform.vo.PlatformInfoDto;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.service.license.LicenseService;
import com.cmsr.onebase.module.system.service.mail.MailAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.cmsr.onebase.module.system.controller.admin.platform.vo.PlatformInfoReqVo;
import com.cmsr.onebase.module.system.controller.admin.platform.vo.PlatformInfoRespVo;

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
    // @PreAuthorize("@ss.hasPermission('system:platform-admin:query')")
    public CommonResult<PlatformInfoRespVo> listPlatformInfos() {
        // TODO: 实现获取平台信息列表逻辑
        PlatformInfoRespVo respVo = new PlatformInfoRespVo();

        // 读取license.json文件内容
        ObjectMapper objectMapper = new ObjectMapper();
        // 添加JavaTimeModule以支持LocalDateTime等Java 8时间类型
        objectMapper.registerModule(new JavaTimeModule());
        try {
            PlatformInfoDto platformInfoDto = objectMapper.readValue(new ClassPathResource("license.json").getFile(),
                    PlatformInfoDto.class);
            respVo.setPlatformInfoDto(platformInfoDto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<LicenseDO> list = licenseService.getSimpleLicenseList();
        respVo.setLicenseList(list);
        return success(respVo);
    }
    
    /**
     * 创建凭证
     */
    @PostMapping("/license/create")
    @Operation(summary = "创建凭证")
    public Object createLicense(@RequestBody PlatformInfoReqVo reqVo) {
        // TODO: 实现创建凭证逻辑
        return null;
    }
}