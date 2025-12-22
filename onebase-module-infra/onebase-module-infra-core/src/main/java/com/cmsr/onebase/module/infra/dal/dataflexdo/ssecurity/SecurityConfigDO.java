package com.cmsr.onebase.module.infra.dal.dataflexdo.ssecurity;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
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
@NoArgsConstructor
@AllArgsConstructor
@Table("infra_security_config")
public class SecurityConfigDO extends BaseTenantEntity {

    public static final String TENANT_ID = "tenant_id";
    public static final String CONFIG_KEY = "config_key";
    public static final String CONFIG_VALUE = "config_value";

    /**
     * 租户ID
     */
    @Column(value = TENANT_ID)
    private Long tenantId;

    /**
     * 配置键
     */
    @Column(value = CONFIG_KEY)
    private String configKey;

    /**
     * 配置值
     */
    @Column(value = CONFIG_VALUE)
    private String configValue;

}
