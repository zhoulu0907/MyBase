package com.cmsr.onebase.module.metadata.core.dal.dataobject.validation;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字段类型比较规则表 DO
 * 用于定义字段类型+操作符对应的可选目标字段类型
 *
 * @author GitHub Copilot
 * @date 2025-12-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "metadata_field_comparison_rules")
public class MetadataFieldComparisonRulesDO extends BaseEntity {

    /**
     * 源字段类型ID
     */
    @Column(value = "source_field_type_id", comment = "源字段类型ID")
    private Long sourceFieldTypeId;

    /**
     * 校验类型ID（操作符）
     */
    @Column(value = "validation_type_id", comment = "校验类型ID")
    private Long validationTypeId;

    /**
     * 目标字段类型ID
     */
    @Column(value = "target_field_type_id", comment = "目标字段类型ID")
    private Long targetFieldTypeId;

}
