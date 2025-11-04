package com.cmsr.onebase.module.app.core.dto.app;

import lombok.Data;

/**
 * @Date：2025/7/22 17:50
 */
@Data
public class ApplicationDTO  {

    private  Long id;

    private String appUid;

    private String appName;

    private String appCode;

    private String appMode;

    private String themeColor;

    private String iconName;

    private String iconColor;

    private String versionNumber;

    private String versionUrl;

    private Integer appStatus;

    private String description;

    private Integer publishModel;

}
