package com.cmsr.onebase.module.metadata.core.dal.dataobject.number;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
/**
 * 自动编号-周期计数状态 DO
 * 对应表：metadata_auto_number_state
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_auto_number_state")
public class MetadataAutoNumberStateDO extends TenantBaseDO {

    public static final String CONFIG_ID = "config_id";
    public static final String PERIOD_KEY = "period_key";
    public static final String CURRENT_VALUE = "current_value";
    public static final String LAST_RESET_TIME = "last_reset_time";
    public static final String APP_ID = "app_id";

    @Column(name = CONFIG_ID)
    private Long configId;
    @Column(name = PERIOD_KEY)
    private String periodKey;
    @Column(name = CURRENT_VALUE)
    private Long currentValue;
    @Column(name = LAST_RESET_TIME)
    private java.time.LocalDateTime lastResetTime;
    @Column(name = APP_ID)
    private Long appId;
}


