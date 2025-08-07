package com.cmsr.onebase.module.system.controller.admin.platform;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.controller.admin.platform.vo.PlatformInfoReqVo;
import com.cmsr.onebase.module.system.controller.admin.platform.vo.PlatformInfoRespVo;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.tenant.TenantStatusEnum;
import com.cmsr.onebase.module.system.enums.user.UserStatusEnum;
import com.cmsr.onebase.module.system.service.license.LicenseService;
import com.cmsr.onebase.module.system.service.tenant.TenantService;
import com.cmsr.onebase.module.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @Resource
    private TenantService tenantService;

    @Resource
    private AdminUserService adminUserService;

    /**
     * 创建平台信息
     */
    @PostMapping("/create")
    public Object createPlatformInfo(@RequestBody PlatformInfoReqVo reqVo) {
        // TODO: 实现创建平台信息逻辑
        return null;
    }

    /**
     * 更新平台信息
     */
    @PostMapping("/update")
    public Object updatePlatformInfo(@RequestBody PlatformInfoReqVo reqVo) {
        // TODO: 实现更新平台信息逻辑
        return null;
    }

    /**
     * 删除平台信息
     */
    @PostMapping("/delete")
    public Object deletePlatformInfo(@RequestParam Long id) {
        // TODO: 实现删除平台信息逻辑
        return null;
    }

    /**
     * 根据状态查询出enable的license记录
     */
    @GetMapping("/get-platform-info")
    @Operation(summary = "根据状态查询出enable的license记录")
    @PreAuthorize("@ss.hasPermission('system:platform-info:get')")
    public CommonResult<PlatformInfoRespVo> getPlatformInfo() {

        LicenseDO license = licenseService.getLicenseByStatus("enable");
        PlatformInfoRespVo platformInfoRespVo = BeanUtils.toBean(license, PlatformInfoRespVo.class);
        Integer tenantCount = tenantService.getTenantCountByStatus(TenantStatusEnum.NORMAL.getStatus());
        Integer userCount = adminUserService.getUserCountByStatus(UserStatusEnum.NORMAL.getStatus());
        AdminUserDO user = adminUserService.getUser(platformInfoRespVo.getCreator());

        platformInfoRespVo.setAdminUser(user.getUsername());
        platformInfoRespVo.setActualTenantCount(tenantCount);
        platformInfoRespVo.setActualUserCount(userCount);

        return success(platformInfoRespVo);

    }

}