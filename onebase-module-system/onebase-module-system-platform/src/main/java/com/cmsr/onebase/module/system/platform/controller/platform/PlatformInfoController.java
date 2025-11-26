package com.cmsr.onebase.module.system.platform.controller.platform;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.vo.platform.PlatformInfoRespVo;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.permission.AdminTypeEnum;
import com.cmsr.onebase.module.system.enums.tenant.TenantStatusEnum;
import com.cmsr.onebase.module.system.enums.user.UserStatusEnum;
import com.cmsr.onebase.module.system.service.license.LicenseService;
import com.cmsr.onebase.module.system.service.tenant.TenantService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.USER_PASSWORD_NOT_ALLOW_DEL;

/**
 * 平台信息管理控制器
 */
@RestController
@RequestMapping("/system/platform")
@Tag(name = "平台信息管理")
public class PlatformInfoController {

    @Resource
    private LicenseService licenseService;

    @Resource
    private TenantService tenantService;

    @Resource
    private UserService platformUserService;

    /**
     * 根据状态查询出enable的license记录
     */
    @GetMapping("/get-platform-info")
    @Operation(summary = "根据状态查询出enable的license记录")
    @PreAuthorize("@ss.hasPermission('system:platform-info:get')")
    public CommonResult<PlatformInfoRespVo> getPlatformInfo() {

        LicenseDO license = licenseService.getLatestActiveLicense();
        PlatformInfoRespVo platformInfoRespVo = BeanUtils.toBean(license, PlatformInfoRespVo.class);
        Integer tenantCount = tenantService.getTenantCountByStatus(TenantStatusEnum.NORMAL.getStatus());
        Integer userCount = platformUserService.getUserCountByStatus(UserStatusEnum.NORMAL.getStatus());
        AdminUserDO user = platformUserService.getUser(platformInfoRespVo.getCreator());

        platformInfoRespVo.setAdminUser(user.getUsername());
        platformInfoRespVo.setActualTenantCount(tenantCount);
        platformInfoRespVo.setActualUserCount(userCount);

        return success(platformInfoRespVo);

    }


    @PostMapping("/admin/create")
    @Operation(summary = "新增平台管理员用户")
    @PreAuthorize("@ss.hasPermission('system:platform-admin:create')")
    public CommonResult<Long> createPlatformAdmin(@Valid @RequestBody UserInsertReqVO reqVO) {
        Long userId = platformUserService.createPlatformUser(reqVO);
        return success(userId);
    }

    @GetMapping("/admin/page")
    @Operation(summary = "获得平台管理员列表分页")
    @PreAuthorize("@ss.hasPermission('system:platform-admin:query')")
    public CommonResult<PageResult<UserRespVO>> getPlatformAdminPage(@Valid UserPageReqVO pageReqVO) {
        // 获得用户分页列表
        PageResult<AdminUserDO> pageResult = platformUserService.getUserPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, UserRespVO.class));
    }

    @PostMapping("/admin/update-email")
    @Operation(summary = "修改平台管理员邮箱")
    @PreAuthorize("@ss.hasPermission('system:platform-admin:update')")
    public CommonResult<Boolean> updatePlatformAdmin(@Valid @RequestBody UserUpdateEmailReqVO reqVO) {
        platformUserService.updatePlatformUserEmail(reqVO.getId(), reqVO.getEmail());
        return success(true);
    }

    @PostMapping("/admin/update-password")
    @Operation(summary = "重置平台用户密码")
    @PreAuthorize("@ss.hasPermission('system:platform-admin:update-password')")
    public CommonResult<Boolean> updatePlatformUserPassword(@Valid @RequestBody UserUpdatePasswordReqVO reqVO) {
        platformUserService.updateUserPassword(reqVO.getId(), reqVO.getPassword());
        return success(true);
    }

    @GetMapping("/admin/list")
    @Operation(summary = "获得所有平台管理员列表")
    @PreAuthorize("@ss.hasPermission('system:platform-admin:query')")
    public CommonResult<List<UserRespVO>> getPlatformAdminList() {

        // 获取所有平台管理员用户
        List<AdminUserDO> userList = platformUserService.getPlatformAdminListByStatus(UserStatusEnum.NORMAL.getStatus());
        // 转换为响应对象
        List<UserRespVO> respList = BeanUtils.toBean(userList, UserRespVO.class);

        return success(respList);
    }

    @PostMapping("/admin/delete")
    @Operation(summary = "删除平台管理员")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:platform-admin:delete')")
    public CommonResult<Boolean> deletePlatformAdmin(@RequestParam("id") Long id) {
        AdminUserDO adminUserDO = platformUserService.getUser(id);
        if (AdminTypeEnum.SYSTEM.getType().equals(adminUserDO.getAdminType())) {
            throw exception(USER_PASSWORD_NOT_ALLOW_DEL);
        }
        platformUserService.deleteUser(id);
        return success(true);
    }

}