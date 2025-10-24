package com.cmsr.onebase.module.app.api.permission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/24 14:15
 */
@Data
@Schema(description = "应用权限功能定义")
public class PermissionDTO {

    @Schema(description = "权限id")
    private Long id;

    @Schema(description = "菜单id")
    private Long menuId;

    @Schema(description = "页面是否可访问")
    private Integer isPageAllowed;

    @Schema(description = "所有视图可访问")
    private Integer isAllViewsAllowed;

    @Schema(description = "所有字段可操作")
    private Integer isAllFieldsAllowed;

    @Schema(description = "操作权限标签")
    private String operationTags;

}
