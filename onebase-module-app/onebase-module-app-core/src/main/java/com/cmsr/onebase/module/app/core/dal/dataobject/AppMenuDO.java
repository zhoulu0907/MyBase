package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/23 14:31
 */
@Data
@Table(value = "app_menu")
public class AppMenuDO extends BaseBizEntity {

    @Column(value = "menu_uuid", comment = "菜单id")
    private String menuUuid;

    @Column(value = "parent_uuid", comment = "父节点编码")
    private String parentUuid;

    @Column(value = "entity_uuid", comment = "实体id")
    private String entityUuid;

    @Column(value = "menu_code", comment = "菜单编码")
    private String menuCode;

    @Column(value = "menu_sort", comment = "菜单排序")
    private Integer menuSort;

    @Column(value = "menu_type", comment = "菜单类型")
    private Integer menuType;

    @Column(value = "menu_name", comment = "菜单名称")
    private String menuName;

    @Column(value = "menu_icon", comment = "菜单图标")
    private String menuIcon;

    @Column(value = "action_target", comment = "菜单动作")
    private String actionTarget;

    /**
     * PC端是否可见 (1:可见, 0:不可见)
     */
    @Column(value = "is_visible_pc", comment = "PC端是否可见")
    private Integer isVisiblePc;

    /**
     * 移动端是否可见 (1:可见, 0:不可见)
     */
    @Column(value = "is_visible_mobile", comment = "移动端是否可见")
    private Integer isVisibleMobile;

}
