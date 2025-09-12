package com.cmsr.onebase.module.app.build.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "应用管理 - 角色创建 Request VO")
public class AuthRoleCreateRespVO {

    @Schema(description = "角色ID", required = true)
    private Long id;

    @Schema(description = "应用ID", required = true)
    private Long applicationId;

    @Schema(description = "角色编码", required = true)
    private String roleCode;

    @Schema(description = "角色名称", required = true)
    private String roleName;

    @Schema(description = "角色类型，1系统管理员2系统默认用户3用户定义", required = true)
    private Integer roleType;

    @Schema(description = "描述")
    private String description;
}
