package com.cmsr.onebase.module.app.core.vo.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Schema(description = "菜单UUID")
    private String menuUuid;

    @Schema(description = "父节点编码")
    private String parentUuid;

    @Schema(description = "实体UUID")
    private String entityUuid;

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

    @Schema(description = "菜单可见")
    private Integer isVisible;

    @Schema(description = "页面集类型 1-普通表单 2-流程表单 3-工作台")
    private Integer pagesetType;

    @Schema(description = "子菜单")
    public LinkedList<MenuListRespVO> children = null;

    @Schema(description = "过滤标记（辅助字段，不返回给前端）", hidden = true)
    @JsonIgnore
    private boolean filter = false;


    @Schema(description = "页面集类型 1-普通表单 2-流程表单 3-工作台")
    private Integer pageSetType;

}
