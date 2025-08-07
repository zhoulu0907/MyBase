package com.cmsr.onebase.module.app.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "应用管理 - 角色创建 Request VO")
public class AuthRoleCreateReqVO {

    @Schema(description = "应用ID")
    @NotNull(message = "应用ID不能为空")
    private Long applicationId;

    @Schema(description = "角色名称")
    @NotNull(message = "角色名称不能为空")
    private String roleName;

}
