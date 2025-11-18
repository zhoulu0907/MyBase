package com.cmsr.onebase.module.app.core.dal.dataobject.version;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 *                  @Date：2025/7/22 17:54
 */
@Data
@Table(name = "app_version")
public class VersionDO extends TenantBaseDO {

    public static final String APPLICATION_ID = "application_id";

    @Column(name = "application_id", nullable = false, columnDefinition = "BIGINT NOT NULL", comment = "应用ID")
    private Long applicationId;

    @Column(name = "version_name", nullable = false, columnDefinition = "VARCHAR(128) NOT NULL", length = 128, comment = "版本名称")
    private String versionName;

    @Column(name = "version_number", nullable = false, columnDefinition = "VARCHAR(64) NOT NULL", length = 64, comment = "版本号")
    private String versionNumber;

    @Column(name = "version_description", columnDefinition = "TEXT", comment = "版本描述")
    private String versionDescription;

    @Column(name = "environment", columnDefinition = "VARCHAR(128)", length = 128, comment = "环境")
    private String environment;

    @Column(name = "operation_type", columnDefinition = "INT4", comment = "操作类型")
    private Integer operationType;

    @Column(name = "version_url", columnDefinition = "VARCHAR(1024)", length = 1024, comment = "版本URL")
    private String versionURL;
}
