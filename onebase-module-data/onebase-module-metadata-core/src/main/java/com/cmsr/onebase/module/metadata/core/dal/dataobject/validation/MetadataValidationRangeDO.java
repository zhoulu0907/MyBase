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

    /**
     * 范围校验UUID
     * <p>
     * 用于跨应用、跨版本的唯一标识，与 application_id、version_tag 组成联合唯一约束
     */
    @Column(value = "range_uuid", comment = "范围校验UUID")
    private String rangeUuid;

    /**
     * 规则组UUID
     * <p>
     * 关联 metadata_validation_rule_group.group_uuid
     */
    @Column(value = "group_uuid", comment = "规则组UUID")
    private String groupUuid;

    /**
     * 实体UUID
     * <p>
     * 关联 metadata_business_entity.entity_uuid
     */
    @Column(value = "entity_uuid", comment = "实体UUID")
    private String entityUuid;

    /**
     * 字段UUID
     * <p>
     * 关联 metadata_entity_field.field_uuid
     */
    @Column(value = "field_uuid", comment = "字段UUID")
    private String fieldUuid;

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
