package com.cmsr.onebase.module.metadata.dal.dataobject.validation;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 字段校验-范围规则 DO（数值/日期）
 * 对应表：metadata_validation_range
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_validation_range")
public class MetadataValidationRangeDO extends TenantBaseDO {

    public static final String GROUP_ID       = "group_id";
    public static final String ENTITY_ID      = "entity_id";
    public static final String FIELD_ID       = "field_id";
    public static final String IS_ENABLED     = "is_enabled";
    public static final String RANGE_TYPE     = "range_type";
    public static final String MIN_VALUE      = "min_value";
    public static final String MAX_VALUE      = "max_value";
    public static final String MIN_DATE       = "min_date";
    public static final String MAX_DATE       = "max_date";
    public static final String INCLUDE_MIN    = "include_min";
    public static final String INCLUDE_MAX    = "include_max";
    public static final String PROMPT_MESSAGE = "prompt_message";
    public static final String RUN_MODE       = "run_mode";
    public static final String APP_ID         = "app_id";

    public MetadataValidationRangeDO setId(Long id) {
        super.setId(id);
        return this;
    }

    private Long groupId;
    private Long entityId;
    private Long fieldId;
    private Integer isEnabled;
    private String rangeType; // NUMBER | DATE
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private LocalDateTime minDate;
    private LocalDateTime maxDate;
    private Integer includeMin;
    private Integer includeMax;
    private String promptMessage;
    private Integer runMode;
    private Long appId;
}
