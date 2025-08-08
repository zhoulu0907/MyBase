package com.cmsr.onebase.module.app.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/7 9:36
 */
@Data
@Schema(description = "应用管理 - 角色权限 Response VO")
public class AuthPermissionVO {

    @Schema(description = "主键Id")
    private Long id;

    @Schema(description = "页面是否可访问")
    private Boolean pageAllowed;

    @Schema(description = "所有实体可访问")
    private Boolean allEntitiesAllowed;

    @Schema(description = "所有字段可操作")
    private Boolean allFieldsAllowed;

}
