package com.cmsr.onebase.module.app.build.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/23 13:52
 */
@Schema(description = "应用管理 - 应用菜单分组创建/修改 Request VO")
@Data
public class MenuCreateReqVO {

    @Schema(description = "应用ID")
    @NotBlank(message = "应用ID不能为空")
    private Long applicationId;

    @Schema(description = "菜单类型")
    @NotBlank(message = "菜单类型不能为空")
    private Integer menuType;

    @Schema(description = "父菜单Id")
    private Long parentId;

    @Schema(description = "菜单名称")
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    @Schema(description = "菜单图标")
    @NotBlank(message = "菜单图标不能为空")
    private String menuIcon;

    @Schema(description = "页面集类型 1-普通表单 2-流程表单 3-工作台")
    private Integer pageSetType;

    @Schema(description = "实体编码")
    private Long entityId;
}
