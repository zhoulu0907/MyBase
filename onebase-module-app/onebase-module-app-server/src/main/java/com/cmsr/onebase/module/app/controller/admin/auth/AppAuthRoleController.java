package com.cmsr.onebase.module.app.controller.admin.auth;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRoleListRespVO;
import com.cmsr.onebase.module.app.service.auth.AppAuthRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author：huangjie
 * @Date：2025/8/7 8:57
 */
@Tag(name = "应用管理-角色管理")
@RestController
@RequestMapping("/app/auth-role")
@Validated
public class AppAuthRoleController {

    @Resource
    private AppAuthRoleService authRoleService;

    /**
     * 获取角色列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取角色列表")
    public CommonResult<AuthRoleListRespVO> getAuthRoleList(
            @RequestParam(value = "applicationId", required = false) Long applicationId) {
        return CommonResult.success(authRoleService.getAuthRoleList(applicationId));
    }
}
