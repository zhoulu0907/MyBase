package com.cmsr.onebase.module.metadata.dal.dataobject.number;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.*;

/**
 * 自动编号-手动重置日志 DO
 * 对应表：metadata_auto_number_reset_log
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_auto_number_reset_log")
public class MetadataAutoNumberResetLogDO extends TenantBaseDO {

    public static final String CONFIG_ID = "config_id";
    public static final String PERIOD_KEY = "period_key";
    public static final String PREV_VALUE = "prev_value";
    public static final String NEXT_VALUE = "next_value";
    public static final String RESET_REASON = "reset_reason";
    public static final String OPERATOR = "operator";
    public static final String RESET_TIME = "reset_time";
    public static final String APP_ID = "app_id";

    @Column(name = CONFIG_ID)
    private Long configId;
    @Column(name = PERIOD_KEY)
    private String periodKey;
    @Column(name = PREV_VALUE)
    private Long prevValue;
    @Column(name = NEXT_VALUE)
    private Long nextValue;
    @Column(name = RESET_REASON)
    private String resetReason;
    @Column(name = OPERATOR)
    private Long operator;
    @Column(name = RESET_TIME)
    private java.time.LocalDateTime resetTime;
    @Column(name = APP_ID)
    private Long appId;
}


