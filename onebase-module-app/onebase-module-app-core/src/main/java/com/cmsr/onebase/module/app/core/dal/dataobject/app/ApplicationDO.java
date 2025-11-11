package com.cmsr.onebase.module.app.core.dal.dataobject.app;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 *                  @Date：2025/7/22 17:50
 */
@Data
@Table(name = "app_application")
public class ApplicationDO extends TenantBaseDO {
    public static final String APP_UID = "app_uid";
    public static final String APP_NAME = "app_name";
    public static final String APP_CODE = "app_code";
    public static final String APP_MODE = "app_mode";
    public static final String THEME_COLOR = "theme_color";
    public static final String ICON_NAME = "icon_name";
    public static final String ICON_COLOR = "icon_color";
    public static final String VERSION_NUMBER = "version_number";
    public static final String VERSION_URL = "version_url";
    public static final String APP_STATUS = "app_status";
    public static final String DESCRIPTION = "description";
    public static final String PUBLISH_MODEL = "publish_model";
    public static final String ID = "id";


    @Column(name = "app_uid", columnDefinition = "VARCHAR(16)", length = 256, comment = "应用uid(自动生成短码)")
    private String appUid;

    @Column(name = "app_name", columnDefinition = "VARCHAR(128) NOT NULL", nullable = false, length = 128, comment = "应用名称")
    private String appName;

    @Column(name = "app_code", columnDefinition = "VARCHAR(16) NOT NULL", nullable = false, length = 256, comment = "应用编码")
    private String appCode;

    @Column(name = "app_mode", columnDefinition = "VARCHAR(32)", length = 32, comment = "应用模式")
    private String appMode;

    @Column(name = "theme_color", columnDefinition = "VARCHAR(32)", length = 32, comment = "主题色")
    private String themeColor;

    @Column(name = "icon_name", columnDefinition = "VARCHAR(256)", length = 256, comment = "图标名称")
    private String iconName;

    @Column(name = "icon_color", columnDefinition = "VARCHAR(64)", length = 32, comment = "图标颜色")
    private String iconColor;

    @Column(name = "version_number", columnDefinition = "VARCHAR(64)", length = 64, comment = "版本号")
    private String versionNumber;

    @Column(name = "version_url", columnDefinition = "VARCHAR(1024)", length = 1024, comment = "版本URL")
    private String versionUrl;

    @Column(name = "app_status", columnDefinition = "INT4 NOT NULL", nullable = false, comment = "状态")
    private Integer appStatus;

    @Column(name = "description", columnDefinition = "VARCHAR(1024)", length = 1024, comment = "描述")
    private String description;

    @Column(name = "publish_model", columnDefinition = "VARCHAR(256)", comment = "发布模式/内部模式")
    private String publishModel;

}
