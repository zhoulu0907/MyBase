package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/23 14:31
 */
@Data
@Table(value = "app_menu")
public class MenuDO extends BaseAppEntity {

    @Column(value = "parent_id", comment = "父节点编码")
    private Long parentId;

    @Column(value = "entity_id", comment = "实体id")
    private Long entityId;

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

    @Column(value = "is_visible", comment = "是否可见")
    private Integer isVisible;

}
