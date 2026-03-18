package com.cmsr.onebase.module.app.api.version.dto;

import lombok.Data;

/**
 * @Author：yuxin
 * @Date：2026/03/18
 */
@Data
public class AppVersionDTO {

    private Long id;

    private String versionName;

    private String versionNumber;

    private String versionDescription;

    private String environment;

    private Integer operationType;

    private String versionURL;

    private Integer versionType;

    private Integer appThirdUserEnable;

    private Long applicationId;

    private Long tenantId;

}
