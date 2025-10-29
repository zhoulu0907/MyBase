package com.cmsr.onebase.module.app.build.vo.auth;

import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/20 15:42
 */
@Schema(description = "应用管理 - 权限 页面是否可访问 Request VO")
@Data
public class AuthUpdatePageAllowedReqVO {

    @Schema(description = "应用管理 - 权限基础参数")
    @NotNull(message = "应用管理 - 权限基础参数不能为空")
    private AuthPermissionReq permissionReq;

    @Schema(description = "页面是否可访问")
    @NotNull(message = "页面是否可访问不能为空")
    private Integer isPageAllowed;


}
