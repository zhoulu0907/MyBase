package com.cmsr.onebase.module.app.dal.dataobject.app;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/22 17:50
 */
@Data
@Table(name = "app_application")
public class ApplicationDO extends TenantBaseDO {

    @Column(name = "app_name", nullable = false, length = 128, comment = "应用名称")
    private String appName;

    @Column(name = "app_code", nullable = false, length = 256, comment = "应用编码")
    private String appCode;

    @Column(name = "app_mode", length = 32, comment = "应用模式")
    private String appMode;

    @Column(name = "theme_color", length = 32, comment = "主题色")
    private String themeColor;

    @Column(name = "icon_name", length = 256, comment = "图标名称")
    private String iconName;

    @Column(name = "icon_color", length = 32, comment = "图标颜色")
    private String iconColor;

    @Column(name = "version_number", nullable = false, length = 64, comment = "版本号")
    private String versionNumber;

    @Column(name = "datasource_id", nullable = false, comment = "数据源ID")
    private Long datasourceId;

    @Column(name = "status", nullable = false, comment = "状态")
    private Integer status;

    @Column(name = "description", length = 1024, comment = "描述")
    private String description;
}