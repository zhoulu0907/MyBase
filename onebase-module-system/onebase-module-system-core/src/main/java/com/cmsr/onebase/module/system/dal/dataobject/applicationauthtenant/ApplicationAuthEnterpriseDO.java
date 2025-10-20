package com.cmsr.onebase.module.system.dal.dataobject.applicationauthtenant;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用授权企业表 DO
 */
@Data
@Entity
@Table(name = "system_application_auth_enterprise")
@EqualsAndHashCode(callSuper = true)
public class ApplicationAuthEnterpriseDO extends BaseDO {

    /**
     * 编号
     */
    @Column(name = "id", length = 19, nullable = false)
    private Long id;

    /**
     * 应用id
     */
    @Column(name = "application_id", length = 10, nullable = false)
    private Integer applicationId;

    /**
     * 企业id
     */
    @Column(name = "enterprise_id", length = 10, nullable = false)
    private Integer enterpriseId;

    /**
     * 空间id
     */
    @Column(name = "tenant_id", length = 10)
    private Integer tenantId;

    /**
     * 锁标识
     */
    @Column(name = "lock_version", length = 19)
    private Long lockVersion;
}