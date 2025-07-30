package com.cmsr.onebase.module.app.dal.dataobject.app;

import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/23 16:51
 */
@Data
@Table(name = "app_version_menu")
public class VersionMenuDO extends BaseDO {

    /**
     * 应用Id
     */
    @Column(name = "application_id", nullable = false, length = 19, comment = "应用Id")
    private Long applicationId;

    /**
     * 版本Id
     */
    @Column(name = "version_id", nullable = false, length = 19, comment = "版本Id")
    private Long versionId;

    /**
     * 菜单父节点Id
     */
    @Column(name = "parent_id", nullable = false, length = 64, comment = "菜单父节点Id")
    private String parentId;

    /**
     * 菜单uuid
     */
    @Column(name = "menu_uuid", nullable = false, length = 64, comment = "菜单uuid")
    private String menuUuid;

    /**
     * 菜单排序
     */
    @Column(name = "menu_sort", nullable = false, columnDefinition = "int4 DEFAULT 0", comment = "菜单排序")
    private Integer menuSort;

    /**
     * 菜单类型
     */
    @Column(name = "menu_type", nullable = false, length = 64, comment = "菜单类型")
    private String menuType;

    /**
     * 菜单名称
     */
    @Column(name = "menu_name", nullable = false, length = 64, comment = "菜单名称")
    private String menuName;

    /**
     * 菜单图标
     */
    @Column(name = "menu_icon", nullable = false, length = 64, comment = "菜单图标")
    private String menuIcon;
}