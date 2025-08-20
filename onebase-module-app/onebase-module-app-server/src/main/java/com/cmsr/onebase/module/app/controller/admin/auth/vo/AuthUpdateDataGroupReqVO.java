package com.cmsr.onebase.module.app.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/20 15:42
 */
@Schema(description = "应用管理 - 权限 数据权限 Request VO")
@Data
public class AuthUpdateDataGroupReqVO extends AuthPermissionReqVO {

    @Schema(description = "数据访问")
    @NotNull(message = "数据访问不能为空")
    private AuthDataGroupVO authDataGroup;

}
