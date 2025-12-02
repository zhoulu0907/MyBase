package com.cmsr.onebase.module.metadata.core.dal.dataobject.validation;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据权限-操作符号与字段类型的关联表 DO
 *
 * 对应表：metadata_permit_ref_otft
 *
 * @author bty418
 * @date 2025-08-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "metadata_permit_ref_otft")
public class MetadataPermitRefOtftDO extends BaseEntity {

    /**
     * 字段类型Id
     */
    @Column(value = "field_type_id", comment = "字段类型Id")
    private Long fieldTypeId;

    /**
     * 操作符号Id
     */
    @Column(value = "validation_type_id", comment = "操作符号Id")
    private Long validationTypeId;

    /**
     * 排序
     */
    @Column(value = "sort_order", comment = "排序")
    private Integer sortOrder;
}


