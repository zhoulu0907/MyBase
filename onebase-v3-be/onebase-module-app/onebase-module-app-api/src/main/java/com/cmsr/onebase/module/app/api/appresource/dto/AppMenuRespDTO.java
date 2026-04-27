package com.cmsr.onebase.module.app.api.appresource.dto;


import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * 应用菜单响应DTO
 *
 * @author liyang
 * @date 2025-12-03
 */
@Data
public class AppMenuRespDTO {
    private String menuUuid;

    private String parentUuid;

    private String entityUuid;

    private String menuCode;

    private Integer menuSort;

    private Integer menuType;

    private String menuName;

    private String menuIcon;

    private String actionTarget;

    private Integer isVisible;

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
