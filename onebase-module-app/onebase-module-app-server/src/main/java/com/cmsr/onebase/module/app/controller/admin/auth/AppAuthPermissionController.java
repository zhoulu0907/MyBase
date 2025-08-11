package com.cmsr.onebase.module.app.controller.admin.auth;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthPermissionDetailVO;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthPermissionReqVO;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthPermissionVO;
import com.cmsr.onebase.module.app.service.auth.AppAuthPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author：huangjie
 * @Date：2025/8/7 8:57
 */
@Tag(name = "应用管理-权限管理")
@RestController
@RequestMapping("/app/auth-permission")
@Validated
public class AppAuthPermissionController {

    @Resource
    private AppAuthPermissionService authPermissionService;

    /**
     * 获取角色-菜单的权限
     */
    @GetMapping("/get")
    @Operation(summary = "获取角色-菜单的权限")
    public CommonResult<AuthPermissionDetailVO> getPermission(@Valid AuthPermissionReqVO reqVO) {
        return CommonResult.success(authPermissionService.getPermission(reqVO));
    }

    /**
     * 更新角色-菜单的权限
     */
    @PostMapping("/update")
    @Operation(summary = "更新角色-菜单的权限")
    public CommonResult<Boolean> updatePermission(@Valid @RequestBody AuthPermissionDetailVO detailVO) {
        authPermissionService.updatePermission(detailVO);
        return CommonResult.success(true);
    }


}
