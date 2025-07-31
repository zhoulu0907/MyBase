package com.cmsr.onebase.module.app.dal.dataobject.app;

import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/23 14:31
 */
@Data
@Table(name = "app_application_menu")
public class ApplicationMenuDO extends BaseDO {

    @Column(name = "application_id", nullable = false, comment = "应用Id")
    private Long applicationId;

    @Column(name = "parent_uuid", nullable = false, length = 64, comment = "父节点Id")
    private String parentUuid;

    @Column(name = "menu_uuid", length = 64, comment = "菜单uuid")
    private String menuUuid;

    @Column(name = "menu_sort", nullable = false, columnDefinition = "integer default 0", comment = "菜单排序")
    private Integer menuSort;

    @Column(name = "menu_type", nullable = false, length = 64, comment = "菜单类型")
    private String menuType;

    @Column(name = "menu_name", nullable = false, length = 64, comment = "菜单名称")
    private String menuName;

    @Column(name = "menu_icon", nullable = false, length = 64, comment = "菜单图标")
    private String menuIcon;

}