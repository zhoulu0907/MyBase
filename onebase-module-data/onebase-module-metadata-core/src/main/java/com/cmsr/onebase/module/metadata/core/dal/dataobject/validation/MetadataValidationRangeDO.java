package com.cmsr.onebase.module.metadata.core.dal.dataobject.validation;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 字段校验-范围规则 DO（数值/日期）
 * 对应表：metadata_validation_range
 *
 * @author bty418
 * @date 2025-08-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "metadata_validation_range")
public class MetadataValidationRangeDO extends BaseTenantEntity {

    @Column(value = "group_id", comment = "规则组ID")
    private Long groupId;

    @Column(value = "entity_id", comment = "实体ID")
    private Long entityId;

    @Column(value = "field_id", comment = "字段ID")
    private Long fieldId;

    @Column(value = "is_enabled", comment = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;

    @Column(value = "range_type", comment = "范围类型：NUMBER | DATE")
    private String rangeType;

    @Column(value = "min_value", comment = "最小值")
    private BigDecimal minValue;

    @Column(value = "max_value", comment = "最大值")
    private BigDecimal maxValue;

    @Column(value = "min_date", comment = "最小日期")
    private LocalDateTime minDate;

    @Column(value = "max_date", comment = "最大日期")
    private LocalDateTime maxDate;

    @Column(value = "include_min", comment = "是否包含最小值")
    private Integer includeMin;

    @Column(value = "include_max", comment = "是否包含最大值")
    private Integer includeMax;

    @Column(value = "prompt_message", comment = "提示信息")
    private String promptMessage;

    @Column(value = "version_tag", comment = "版本标识")
    private Long versionTag;

    @Column(value = "application_id", comment = "应用ID")
    private Long applicationId;
}
