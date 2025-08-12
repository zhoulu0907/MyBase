package com.cmsr.onebase.module.app.controller.admin.menu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/23 13:52
 */
@Schema(description = "应用管理 - 应用菜单分组创建/修改 Request VO")
@Data
public class MenuCreateRespVO {

    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "父菜单编码")
    private String parentCode;

    @Schema(description = "实体ID")
    private Long entityId;

    @Schema(description = "菜单编码")
    private String menuCode;

    @Schema(description = "菜单排序")
    private Integer menuSort;

    @Schema(description = "菜单类型")
    private Integer menuType;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "菜单图标")
    private String menuIcon;

    @Schema(description = "菜单动作")
    private String actionTarget;

    @Schema(description = "是否可见")
    private Boolean visible;

}
