package com.cmsr.onebase.framework.tenant.core.db;

import com.cmsr.onebase.framework.data.base.BaseDO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 拓展多租户的 BaseDO 基类
 *
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class TenantBaseDO extends BaseDO {
    public static final String TENANT_ID = "tenant_id";
    
    /**
     * 多租户编号
     */
    @Column(name = "tenant_id")
    private Long tenantId;

}
