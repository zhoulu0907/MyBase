package com.cmsr.onebase.module.app.core.dal.dataobject.version;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 *                  @Date：2025/7/23 16:54
 */
@Data
@Table(name = "app_version_resource")
public class VersionResourceDO extends TenantBaseDO {

    @Column(name = "application_id", nullable = false, columnDefinition = "BIGINT NOT NULL", comment = "应用ID")
    private Long applicationId;

    @Column(name = "version_id", nullable = false, columnDefinition = "BIGINT NOT NULL", comment = "版本ID")
    private Long versionId;

    @Column(name = "res_type", nullable = false, length = 64, columnDefinition = "VARCHAR(64) NOT NULL", comment = "协议类型")
    private String resType;

    @Column(name = "res_data", nullable = false, columnDefinition = "TEXT NOT NULL", comment = "资源数据")
    private String resData;
}
