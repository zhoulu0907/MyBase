package com.cmsr.onebase.module.app.runtime.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.LinkedList;

/**
 * @Author：huangjie
 * @Date：2025/7/24 13:52
 */
@Schema(description = "应用管理 - 应用菜单列表 Resp VO")
@Data
public class MenuListRespVO {

    @Schema(description = "菜单id")
    private Long id;

    @Schema(description = "父节点编码")
    private Long parentId;

    @Schema(description = "实体Id")
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

    @Schema(description = "是否可见")
    private Integer isVisible;

    @Schema(description = "子菜单")
    public LinkedList<MenuListRespVO> children = null;

}
