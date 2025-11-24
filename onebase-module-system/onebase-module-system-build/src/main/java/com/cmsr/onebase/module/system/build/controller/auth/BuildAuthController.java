package com.cmsr.onebase.module.system.build.controller.auth;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.security.config.SecurityProperties;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.module.system.convert.auth.AuthConvert;
import com.cmsr.onebase.module.system.dal.dataobject.permission.MenuDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.logger.LoginLogTypeEnum;
import com.cmsr.onebase.module.system.service.auth.AdminAuthService;
import com.cmsr.onebase.module.system.service.permission.MenuService;
import com.cmsr.onebase.module.system.service.permission.PermissionService;
import com.cmsr.onebase.module.system.service.permission.RoleService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.auth.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertSet;
import static com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 *  编辑态登录认证相关接口
 *  1. 空间登录 tenant-login
 *  2. 企业登录 corp-login
 *
 *  @author matianyu
 *  @date 2025-11
 */
@Tag(name = "管理后台 - 认证")
@RestController
@RequestMapping("/system/auth")
@Validated
@Slf4j
public class BuildAuthController {

    @Resource
    private AdminAuthService authService;
    @Resource
    private UserService      userService;
    @Resource
    private RoleService      roleService;
    @Resource
    private MenuService menuService;
    @Resource
    private PermissionService permissionService;

    @Resource
    private SecurityProperties securityProperties;

    @PostMapping("/admin-login")
    @PermitAll
    @Operation(summary = "使用账号密码登录")
    @Deprecated //todo 之后删除
    public CommonResult<AuthLoginRespVO> adminLogin(@RequestBody @Valid UserLoginReqVO reqVO) {
        return success(authService.adminLogin(reqVO));
    }

    @PostMapping("/login")
    @PermitAll
    @Operation(summary = "使用账号密码登录")
    @Deprecated // todo 之后删除
    public CommonResult<AuthLoginRespVO> login(@RequestBody @Valid AuthLoginReqVO reqVO) {
        return success(authService.login(reqVO));
    }

    @PostMapping("/tenant-login")
    @PermitAll
    @Operation(summary = "空间登录（账密）")
    public CommonResult<AuthLoginRespVO> tenantLogin(@RequestBody @Valid AuthLoginReqVO reqVO) {
        return success(authService.login(reqVO));
    }

    @PostMapping("/corp-login")
    @PermitAll
    @Operation(summary = "企业登录（手机号）")
    public CommonResult<AuthLoginRespVO> corpLogin(@RequestBody @Valid CorpAuthLoginReqVO reqVO) {
        return success(authService.corpLogin(reqVO));
    }

    @PostMapping("/logout")
    @PermitAll
    @Operation(summary = "登出系统")
    public CommonResult<Boolean> logout(HttpServletRequest request) {
        String token = SecurityFrameworkUtils.obtainAuthorization(request,
                securityProperties.getTokenHeader(), securityProperties.getTokenParameter());
        if (StrUtil.isNotBlank(token)) {
            authService.logout(token, LoginLogTypeEnum.LOGOUT_SELF.getType());
        }
        return success(true);
    }

    @PostMapping("/refresh-token")
    @PermitAll
    @Operation(summary = "刷新令牌")
    @Parameter(name = "refreshToken", description = "刷新令牌", required = true)
    public CommonResult<AuthLoginRespVO> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        return success(authService.refreshToken(refreshToken));
    }

    @GetMapping("/get-permission-info")
    @Operation(summary = "获取登录用户的权限信息")
    public CommonResult<AuthPermissionInfoRespVO> getPermissionInfo(@RequestParam(value = "code", required = false) String code) {
        // 1.1 获得用户信息
        AdminUserDO user = userService.getUser(getLoginUserId());
        if (user == null) {
            return success(null);
        }
        // 1.2 获得角色列表
        Set<Long> roleIds = permissionService.getRoleIdsListByUserId(getLoginUserId());
        if (CollUtil.isEmpty(roleIds)) {
            return success(AuthConvert.INSTANCE.convert(user, Collections.emptyList(), Collections.emptyList(), code));
        }
        List<RoleDO> roles = roleService.getRoleList(roleIds);
        roles.removeIf(role -> !CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus())); // 移除禁用的角色

        // 1.3 获得菜单列表
        Set<Long> menuIds = permissionService.getRoleMenuListByRoleId(convertSet(roles, RoleDO::getId));
        List<MenuDO> menuList = menuService.getAllActiveMenuList(menuIds);
        // menuList = menuService.filterDisableMenus(menuList);

        // 2. 拼接结果返回
        return success(AuthConvert.INSTANCE.convert(user, roles, menuList, code));
    }

    @PostMapping("/register")
    @PermitAll
    @Operation(summary = "注册用户")
    public CommonResult<AuthLoginRespVO> register(@RequestBody @Valid AuthRegisterReqVO registerReqVO) {
        return success(authService.register(registerReqVO));
    }

    // ========== 短信登录相关 ==========

    @PostMapping("/sms-login")
    @PermitAll
    @Operation(summary = "使用短信验证码登录")
    public CommonResult<AuthLoginRespVO> smsLogin(@RequestBody @Valid AuthSmsLoginReqVO reqVO) {
        return success(authService.smsLogin(reqVO));
    }

    @PostMapping("/send-sms-code")
    @PermitAll
    @Operation(summary = "发送手机验证码")
    public CommonResult<Boolean> sendLoginSmsCode(@RequestBody @Valid AuthSmsSendReqVO reqVO) {
        authService.sendSmsCode(reqVO);
        return success(true);
    }

    @PostMapping("/reset-password")
    @PermitAll
    @Operation(summary = "重置密码")
    public CommonResult<Boolean> resetPassword(@RequestBody @Valid AuthResetPasswordReqVO reqVO) {
        authService.resetPassword(reqVO);
        return success(true);
    }
}