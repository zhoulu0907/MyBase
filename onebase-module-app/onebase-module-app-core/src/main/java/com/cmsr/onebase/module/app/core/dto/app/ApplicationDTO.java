package com.cmsr.onebase.module.app.core.dto.app;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 *                  @Date：2025/7/22 17:50
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
