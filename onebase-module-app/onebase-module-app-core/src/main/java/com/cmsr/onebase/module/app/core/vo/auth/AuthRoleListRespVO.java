package com.cmsr.onebase.module.app.core.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/7 9:06
 */
@Data
@Schema(description = "应用管理 - 角色 Request VO")
public class AuthRoleListRespVO {

    @Schema(description = "角色ID")
    private Long id;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "角色类型")
    private Integer roleType;

}
