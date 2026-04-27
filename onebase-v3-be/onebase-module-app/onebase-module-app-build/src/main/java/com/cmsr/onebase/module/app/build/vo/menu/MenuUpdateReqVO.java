package com.cmsr.onebase.module.app.build.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/9/4 14:00
 */
@Schema(description = "应用管理 - 应用菜单更新 Request VO")
@Data
public class MenuUpdateReqVO {

    @Schema(description = "菜单ID")
    @NotNull(message = "菜单ID不能为空")
    private Long id;

    @Schema(description = "菜单名称")
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    @Schema(description = "菜单图标")
    @NotBlank(message = "菜单图标不能为空")
    private String menuIcon;

    @Schema(description = "iframe URL")
    private String iframeUrl;
}