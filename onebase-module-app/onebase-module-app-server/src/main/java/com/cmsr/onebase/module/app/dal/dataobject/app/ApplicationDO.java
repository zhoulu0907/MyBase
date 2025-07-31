package com.cmsr.onebase.module.app.dal.dataobject.app;

import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/22 17:50
 */
@Data
@Table(name = "app_application")
public class ApplicationDO extends BaseDO {

    @Column(name = "app_name", nullable = false, length = 128, comment = "应用名称")
    private String appName;

    @Column(name = "app_code", nullable = false, length = 256, comment = "应用编码")
    private String appCode;

    @Column(name = "description", length = 1024, comment = "描述")
    private String description;

    @Column(name = "status", nullable = false, comment = "状态")
    private Integer status;

    @Column(name = "icon_name", length = 256, comment = "图标名称")
    private String iconName;

    @Column(name = "icon_color", length = 32, comment = "图标颜色")
    private String iconColor;

    @Column(name = "version_number", nullable = false, length = 64, comment = "版本号")
    private String versionNumber;

}