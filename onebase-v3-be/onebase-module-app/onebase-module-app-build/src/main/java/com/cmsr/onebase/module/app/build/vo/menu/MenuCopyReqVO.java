package com.cmsr.onebase.module.app.build.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author：mickey
 * @Date：2025/7/30 13:52
 */
@Schema(description = "应用管理 - 应用菜单复制 Request VO")
@Data
public class MenuCopyReqVO {

    @Schema(description = "菜单ID")
    @NotNull(message = "菜单ID不能为空")
    private Long id;

    @Schema(description = "菜单名称")
    @NotNull(message = "菜单名称不能为空")
    private String menuName;

    @Schema(description = "父菜单Id")
    private Long parentId;
}
