package com.cmsr.onebase.module.app.dal.dataobject.app;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/22 17:54
 */
@Data
@Table(name = "app_version")
public class VersionDO extends TenantBaseDO {

    @Column(name = "application_id", nullable = false, comment = "应用ID")
    private Long applicationId;

    @Column(name = "version_name", nullable = false, length = 128, comment = "版本名称")
    private String versionName;

    @Column(name = "version_number", nullable = false, length = 64, comment = "版本号")
    private String versionNumber;

}