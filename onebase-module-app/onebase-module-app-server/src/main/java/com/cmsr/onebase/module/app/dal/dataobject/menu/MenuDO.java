package com.cmsr.onebase.module.app.dal.dataobject.menu;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/23 14:31
 */
@Data
@Table(name = "app_menu")
public class MenuDO extends TenantBaseDO {

    @Column(name = "application_code", nullable = false, comment = "应用code")
    private String applicationCode;

    @Column(name = "parent_code", length = 64, comment = "父节点编码")
    private String parentCode;

    @Column(name = "entity_code", nullable = false, length = 64, comment = "实体code")
    private Long entityCode;

    @Column(name = "menu_code", length = 64, comment = "菜单编码")
    private String menuCode;

    @Column(name = "menu_sort", nullable = false, comment = "菜单排序")
    private Integer menuSort;

    @Column(name = "menu_type", nullable = false, comment = "菜单类型")
    private Integer menuType;

    @Column(name = "menu_name", nullable = false, length = 64, comment = "菜单名称")
    private String menuName;

    @Column(name = "menu_icon", nullable = false, length = 64, comment = "菜单图标")
    private String menuIcon;

    @Column(name = "action_target", nullable = false, length = 64, comment = "菜单动作")
    private String actionTarget;

    @Column(name = "is_visible", nullable = false, comment = "是否可见")
    private Boolean visible;

}
