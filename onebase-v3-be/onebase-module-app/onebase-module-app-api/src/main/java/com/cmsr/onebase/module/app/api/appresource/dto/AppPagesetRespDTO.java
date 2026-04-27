package com.cmsr.onebase.module.app.api.appresource.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppPagesetRespDTO {

    private String pageSetUuid;

    private String menuUuid;

    private String pageSetCode;

    private Integer pageSetType;

    private String mainMetadata;

    private String pageSetName;

    private String displayName;

    private String description;

    private Long applicationId;

    private Long tenantId;

    private Long versionTag;

    private Long id;

    private Long creator;

    private LocalDateTime createTime;

    private Long updater;

    private LocalDateTime updateTime;

    private Long deleted;
}
