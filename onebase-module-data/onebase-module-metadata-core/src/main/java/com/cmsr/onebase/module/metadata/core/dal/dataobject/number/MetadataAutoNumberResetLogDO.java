package com.cmsr.onebase.module.metadata.core.dal.dataobject.number;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 自动编号-手动重置日志 DO
 * 对应表：metadata_auto_number_reset_log
 *
 * @author bty418
 * @date 2025-08-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "metadata_auto_number_reset_log")
public class MetadataAutoNumberResetLogDO extends BaseTenantEntity {

    /**
     * 配置ID
     */
    @Column(value = "config_id", comment = "配置ID")
    private Long configId;

    /**
     * 周期键
     */
    @Column(value = "period_key", comment = "周期键")
    private String periodKey;

    /**
     * 重置前值
     */
    @Column(value = "prev_value", comment = "重置前值")
    private Long prevValue;

    /**
     * 重置后值
     */
    @Column(value = "next_value", comment = "重置后值")
    private Long nextValue;

    /**
     * 重置原因
     */
    @Column(value = "reset_reason", comment = "重置原因")
    private String resetReason;

    /**
     * 操作人
     */
    @Column(value = "operator", comment = "操作人")
    private Long operator;

    /**
     * 重置时间
     */
    @Column(value = "reset_time", comment = "重置时间")
    private LocalDateTime resetTime;

    /**
     * 应用ID
     */
    @Column(value = "application_id", comment = "应用ID")
    private Long applicationId;
}


