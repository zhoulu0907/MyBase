package com.cmsr.onebase.module.app.api.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Date：2025/7/22 17:50
 */
@Data
public class ApplicationDTO {

    private Long id;
    /**
     * 应用uid(自动生成短码)
     */
    private String appUid;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 应用编码
     */
    private String appCode;
    /**
     * 应用模式
     */
    private String appMode;
    /**
     * 应用
     */
    private String themeColor;
    /**
     * 组件图标
     */
    private String iconName;
    /**
     * 组件颜色
     */
    private String iconColor;
    /**
     * 版本号
     */
    private String versionNumber;
    /**
     * 版本url
     */
    private String versionUrl;
    /**
     * 应用发布状态
     */
    private Integer appStatus;
    /**
     * 描述
     */
    private String description;
    /**
     * 发布模式
     */
    private String publishModel;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 租户ID
     */
    private Long tenantId;
}
