package com.cmsr.onebase.module.metadata.dal.dataobject.number;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
/**
 * 自动编号-字段配置 DO
 *
 * 对应表：metadata_auto_number_config
 *
 * @author bty418
 * @date 2025-08-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_auto_number_config")
public class MetadataAutoNumberConfigDO extends TenantBaseDO {

    public static final String FIELD_ID = "field_id";
    public static final String NUMBER_MODE = "number_mode";
    public static final String DIGIT_WIDTH = "digit_width";
    public static final String OVERFLOW_CONTINUE = "overflow_continue";
    public static final String INITIAL_VALUE = "initial_value";
    public static final String RESET_CYCLE = "reset_cycle";
    public static final String IS_ENABLED = "is_enabled";
    public static final String RUN_MODE = "run_mode";
    public static final String APP_ID = "app_id";

    @Column(name = FIELD_ID)
    private Long fieldId;
    @Column(name = NUMBER_MODE)
    private String numberMode;
    @Column(name = DIGIT_WIDTH)
    private Short digitWidth;
    @Column(name = OVERFLOW_CONTINUE)
    private Integer overflowContinue;
    @Column(name = INITIAL_VALUE)
    private Long initialValue;
    @Column(name = RESET_CYCLE)
    private String resetCycle;
    @Column(name = IS_ENABLED)
    private Integer isEnabled;
    @Column(name = RUN_MODE)
    private Integer runMode;
    @Column(name = APP_ID)
    private Long appId;
}


