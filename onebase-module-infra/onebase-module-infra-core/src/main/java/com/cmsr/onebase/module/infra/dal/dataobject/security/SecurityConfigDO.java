package com.cmsr.onebase.module.infra.dal.dataobject.security;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 安全配置表
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "infra_security_config")
public class SecurityConfigDO extends BaseDO {

    public static final String TENANT_ID = "tenant_id";
    public static final String CONFIG_KEY = "config_key";
    public static final String CONFIG_VALUE = "config_value";

    /**
     * 租户ID
     */
    @Column(name = TENANT_ID)
    private Long tenantId;

    /**
     * 配置键
     */
    @Column(name = CONFIG_KEY)
    private String configKey;

    /**
     * 配置值
     */
    @Column(name = CONFIG_VALUE)
    private String configValue;

}
