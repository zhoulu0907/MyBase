package com.cmsr.onebase.module.system.build.controller.user;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import cn.hutool.core.collection.CollUtil;
import com.cmsr.onebase.module.system.vo.user.UserProfileRespVO;
import com.cmsr.onebase.module.system.vo.user.UserProfileUpdatePasswordReqVO;
import com.cmsr.onebase.module.system.vo.user.UserProfileUpdateReqVO;
import com.cmsr.onebase.module.system.convert.user.UserConvert;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.dal.dataobject.dept.PostDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.service.dept.DeptService;
import com.cmsr.onebase.module.system.service.dept.PostService;
import com.cmsr.onebase.module.system.service.permission.PermissionService;
import com.cmsr.onebase.module.system.service.permission.RoleService;
import com.cmsr.onebase.module.system.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;
import static com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 用户个人中心")
@RestController
@RequestMapping("/system/user/profile")
@Validated
@Slf4j
public class TenantUserProfileController {

    @Resource
    private UserService tenantUserService;
    @Resource
    private DeptService tenantDeptService;
    @Resource
    private PostService      postService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private RoleService roleService;

    @GetMapping("/get")
    @Operation(summary = "获得登录用户信息")
    public CommonResult<UserProfileRespVO> getUserProfile() {
        // 获得用户基本信息
        AdminUserDO user = tenantUserService.getUser(getLoginUserId());
        // 获得用户角色
        List<RoleDO> userRoles = roleService.getRoleListFromCache(permissionService.getRoleIdsListByUserId(user.getId()));
        // 获得部门信息
        DeptDO dept = user.getDeptId() != null ? tenantDeptService.getDept(user.getDeptId()) : null;
        // 获得岗位信息
        List<PostDO> posts = CollUtil.isNotEmpty(user.getPostIds()) ? postService.getPostList(user.getPostIds()) : null;
        return success(UserConvert.INSTANCE.convert(user, userRoles, dept, posts));
    }

    @PostMapping("/update")
    @Operation(summary = "修改用户个人信息")
    public CommonResult<Boolean> updateUserProfile(@Valid @RequestBody UserProfileUpdateReqVO reqVO) {
        tenantUserService.updateUserProfile(getLoginUserId(), reqVO);
        return success(true);
    }

    @PostMapping("/update-password")
    @Operation(summary = "修改用户个人密码")
    public CommonResult<Boolean> updateUserProfilePassword(@Valid @RequestBody UserProfileUpdatePasswordReqVO reqVO) {
        tenantUserService.updateUserPassword(getLoginUserId(), reqVO);
        return success(true);
    }

}
