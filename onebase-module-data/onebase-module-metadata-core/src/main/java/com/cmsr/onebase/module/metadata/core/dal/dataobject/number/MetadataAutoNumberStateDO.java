package com.cmsr.onebase.module.metadata.core.dal.dataobject.number;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 自动编号-周期计数状态 DO
 * 对应表：metadata_auto_number_state
 *
 * @author bty418
 * @date 2025-08-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "metadata_auto_number_state")
public class MetadataAutoNumberStateDO extends BaseTenantEntity {

    /**
     * 状态UUID
     * <p>
     * 用于跨应用、跨版本的唯一标识，与 application_id、version_tag 组成联合唯一约束
     */
    @Column(value = "state_uuid", comment = "状态UUID")
    private String stateUuid;

    /**
     * 配置UUID
     * <p>
     * 关联 metadata_auto_number_config.config_uuid
     */
    @Column(value = "config_uuid", comment = "配置UUID")
    private String configUuid;

    /**
     * 周期键
     */
    @Column(value = "period_key", comment = "周期键")
    private String periodKey;

    /**
     * 当前值
     */
    @Column(value = "current_value", comment = "当前值")
    private Long currentValue;

    /**
     * 上次重置时间
     */
    @Column(value = "last_reset_time", comment = "上次重置时间")
    private LocalDateTime lastResetTime;

    /**
     * 应用ID
     */
    @Column(value = "application_id", comment = "应用ID")
    private Long applicationId;
}


