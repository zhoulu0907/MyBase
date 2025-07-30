package com.cmsr.onebase.framework.tenant.core.db;

import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 拓展多租户的 BaseDO 基类
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class TenantBaseDO extends BaseDO {
    public static final String TENANT_ID = "tenant_id";
    
    /**
     * 多租户编号
     */
    @Column(name = "tenant_id")
    private Long tenantId;

}
