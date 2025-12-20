package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 实体类。
 *
 * @author HuangJie
 * @since 2025-12-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("app_navigation")
public class AppNavigationDO extends BaseBizEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(value = "application_id", comment = "应用ID")
    private Long applicationId;

    @Column(value = "version_tag", comment = "版本标签")
    private Long versionTag;

    @Column(value = "theme_color", comment = "主题色")
    private String themeColor;

    @Column(value = "icon_name", comment = "图标名称")
    private String iconName;

    @Column(value = "icon_color", comment = "图标颜色")
    private String iconColor;

    @Column(value = "web_default_menu", comment = "web端默认首页菜单")
    private String webDefaultMenu;

    @Column(value = "web_nav_layout", comment = "web端导航布局")
    private String webNavLayout;

    @Column(value = "mobile_default_menu", comment = "移动端默认首页菜单")
    private String mobileDefaultMenu;

    @Column(value = "mobile_nav_layout", comment = "移动端导航布局")
    private String mobileNavLayout;

}
