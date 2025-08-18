package com.cmsr.onebase.module.metadata.dal.dataobject.validation;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 数据权限-操作符号与字段类型的关联表 DO
 *
 * 对应表：metadata_permit_ref_otft
 *
 * 字段说明：
 * - fieldTypeId：字段类型Id（metadata_component_field_type.id）
 * - validationTypeId：操作符号Id（metadata_validation_type.id）
 * - sortOrder：排序
 *
 * @author bty418
 * @date 2025-08-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_permit_ref_otft")
public class MetadataPermitRefOtftDO extends BaseDO {

    // 列名常量
    public static final String FIELD_TYPE_ID      = "field_type_id";
    public static final String VALIDATION_TYPE_ID = "validation_type_id";
    public static final String SORT_ORDER         = "sort_order";

    public MetadataPermitRefOtftDO setId(Long id) {
        super.setId(id);
        return this;
    }

    /**
     * 字段类型Id
     */
    private Long fieldTypeId;

    /**
     * 操作符号Id
     */
    private Long validationTypeId;

    /**
     * 排序
     */
    private Integer sortOrder;
}


